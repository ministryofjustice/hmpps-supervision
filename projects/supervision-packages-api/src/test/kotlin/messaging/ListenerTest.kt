package uk.gov.justice.digital.hmpps.supervision.messaging

import io.awspring.cloud.sqs.support.converter.SnsNotification
import io.awspring.cloud.sqs.support.converter.SnsNotification.MessageAttribute
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler
import uk.gov.justice.digital.hmpps.supervision.model.HmppsDomainEvent
import uk.gov.justice.digital.hmpps.supervision.telemetry.TelemetryService

@ExtendWith(MockitoExtension::class)
class ListenerTest {
    @Mock
    private lateinit var handler: Handler

    @Mock
    private lateinit var telemetryService: TelemetryService

    @Mock
    private lateinit var taskScheduler: SimpleAsyncTaskScheduler

    private lateinit var listener: Listener

    @BeforeEach
    fun setup() {
        listener = Listener(handler, telemetryService, taskScheduler, "queue-name", 15)
    }

    @Test
    fun `receive tracks telemetry and handles notification message`() {
        val message = HmppsDomainEvent(
            eventType = "person.sentence.changed",
            detailUrl = "https://example.com/detail",
            personReference = HmppsDomainEvent.PersonReference(
                identifiers = listOf(HmppsDomainEvent.PersonIdentifier("CRN", "X123456"))
            )
        )
        val notification = snsNotification(message)

        listener.receive(notification)

        verify(telemetryService).trackEvent(
            "NotificationReceived",
            mapOf(
                "eventType" to "person.sentence.changed",
                "detailUrl" to "https://example.com/detail",
                "CRN" to "X123456"
            ),
            mapOf()
        )
        verify(handler).handle(message)
    }

    private fun snsNotification(message: HmppsDomainEvent) = SnsNotification(
        "Notification",
        "message-id",
        "topic-arn",
        message,
        "2026-05-22T12:00:00Z",
        mapOf("eventType" to MessageAttribute("String", message.eventType)),
        null,
        null,
        null,
        null,
        null,
        null
    )
}
