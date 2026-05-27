package uk.gov.justice.digital.hmpps.supervision.messaging

import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.listener.AsyncAdapterBlockingExecutionFailedException
import io.awspring.cloud.sqs.listener.ListenerExecutionFailedException
import io.awspring.cloud.sqs.listener.Visibility
import io.awspring.cloud.sqs.support.converter.SnsNotification
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.StatusCode
import io.sentry.Sentry
import io.sentry.spring.jakarta.tracing.SentryTransaction
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.dao.CannotAcquireLockException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.CannotGetJdbcConnectionException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler
import org.springframework.stereotype.Component
import org.springframework.transaction.CannotCreateTransactionException
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import uk.gov.justice.digital.hmpps.supervision.model.HmppsDomainEvent
import uk.gov.justice.digital.hmpps.supervision.telemetry.TelemetryMessagingExtensions.extractTelemetryContext
import uk.gov.justice.digital.hmpps.supervision.telemetry.TelemetryMessagingExtensions.notificationReceived
import uk.gov.justice.digital.hmpps.supervision.telemetry.TelemetryMessagingExtensions.withSpan
import uk.gov.justice.digital.hmpps.supervision.telemetry.TelemetryService
import uk.gov.justice.digital.hmpps.supervision.utils.Retry.retry
import java.time.Duration
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletionException

@Component
@ConditionalOnExpression($$"${messaging.consumer.enabled:true} and '${messaging.consumer.queue:}' != ''")
class Listener(
    private val handler: Handler,
    private val telemetryService: TelemetryService,
    private val taskScheduler: SimpleAsyncTaskScheduler,
    @Value($$"${messaging.consumer.queue}") private val queueName: String,
    @Value($$"${messaging.consumer.visibility.extensionInterval:15}") private val visibilityExtensionInterval: Long,
) {
    @SentryTransaction(operation = "messaging")
    @SqsListener($$"${messaging.consumer.queue}")
    fun receive(notification: SnsNotification<HmppsDomainEvent>, visibility: Visibility? = null) {
        visibility.extendWhileRunning {
            notification.messageAttributes
                .extractTelemetryContext()
                .withSpan(
                    this::class.java.simpleName,
                    "RECEIVE ${notification.messageAttributes["eventType"]?.value ?: "unknown event type"}",
                    SpanKind.CONSUMER
                ) {
                    Span.current().setAttribute("queue", queueName)
                    try {
                        telemetryService.notificationReceived(notification)
                        retry(3, RETRYABLE_EXCEPTIONS) { handler.handle(notification.message) }
                    } catch (t: Throwable) {
                        val e = unwrapSqsExceptions(t)
                        Span.current().recordException(e).setStatus(StatusCode.ERROR)
                        Sentry.captureException(e)
                        throw t
                    }
                }
        }
    }

    private fun Visibility?.extendWhileRunning(fn: () -> Unit) {
        // At each interval, reset the visibility timeout to 30 seconds
        val scheduledFuture = this?.let { visibility ->
            taskScheduler.scheduleAtFixedRate({
                visibility.changeToAsync(30).exceptionally { null }.join()
            }, Duration.ofSeconds(visibilityExtensionInterval))
        }

        try {
            fn()
        } finally {
            scheduledFuture?.cancel(false)
        }
    }

    fun unwrapSqsExceptions(e: Throwable): Throwable {
        fun unwrap(e: Throwable) = e.cause ?: e
        var cause = e
        if (cause is CompletionException) {
            cause = unwrap(cause)
        }
        if (cause is AsyncAdapterBlockingExecutionFailedException) {
            cause = unwrap(cause)
        }
        if (cause is ListenerExecutionFailedException) {
            cause = unwrap(cause)
        }
        return cause
    }

    companion object {
        val RETRYABLE_EXCEPTIONS = listOf(
            RestClientException::class,
            CancellationException::class,
            CannotAcquireLockException::class,
            ObjectOptimisticLockingFailureException::class,
            CannotCreateTransactionException::class,
            CannotGetJdbcConnectionException::class,
            UnexpectedRollbackException::class,
            DataIntegrityViolationException::class,
            HttpClientErrorException.TooManyRequests::class,
        )
    }
}
