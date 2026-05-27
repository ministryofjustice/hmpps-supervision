package uk.gov.justice.digital.hmpps.supervision.telemetry

import io.awspring.cloud.sqs.support.converter.SnsNotification
import io.awspring.cloud.sqs.support.converter.SnsNotification.MessageAttribute
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.context.Context
import io.opentelemetry.context.propagation.TextMapGetter
import org.springframework.messaging.MessageHeaders
import uk.gov.justice.digital.hmpps.supervision.model.HmppsDomainEvent

object TelemetryMessagingExtensions {
    fun MessageHeaders.withTelemetryContext(): MessageHeaders {
        val map = this.toMutableMap()
        val context = Context.current().with(Span.current())
        GlobalOpenTelemetry.getPropagators().textMapPropagator
            .inject(context, map) { carrier, key, value -> carrier!![key] = value }
        return MessageHeaders(map)
    }

    fun Map<String, MessageAttribute>.extractTelemetryContext(): Context {
        val getter = object : TextMapGetter<Map<String, MessageAttribute>> {
            override fun keys(carrier: Map<String, MessageAttribute>) = carrier.keys
            override fun get(carrier: Map<String, MessageAttribute>?, key: String) = carrier?.get(key)?.value
        }
        return GlobalOpenTelemetry.getPropagators().textMapPropagator.extract(Context.current(), this, getter)
    }

    fun <T> Context.withSpan(
        scopeName: String,
        spanName: String,
        spanKind: SpanKind = SpanKind.INTERNAL,
        block: () -> T
    ): T {
        val tracer = GlobalOpenTelemetry.getTracer(scopeName)
        val span = tracer.spanBuilder(spanName).setParent(this).setSpanKind(spanKind).startSpan()
        try {
            return span.makeCurrent().use { block() }
        } finally {
            span.end()
        }
    }

    fun TelemetryService.hmppsEventReceived(hmppsEvent: HmppsDomainEvent) {
        trackEvent(
            "NotificationReceived",
            mapOf("eventType" to hmppsEvent.eventType) +
                    (hmppsEvent.detailUrl?.let { mapOf("detailUrl" to it) } ?: mapOf()) +
                    (hmppsEvent.personReference.identifiers.associate { Pair(it.type, it.value) })
        )
    }

    fun <T : Any> TelemetryService.notificationReceived(notification: SnsNotification<T>) {
        val message = notification.message
        if (message is HmppsDomainEvent) {
            hmppsEventReceived(message)
        } else {
            trackEvent("NotificationReceived", mapOf("eventType" to notification.messageAttributes["eventType"]?.value))
        }
    }
}
