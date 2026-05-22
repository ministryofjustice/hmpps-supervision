package uk.gov.justice.digital.hmpps.supervision.model

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import java.time.ZonedDateTime

data class HmppsDomainEvent(
    val eventType: String,
    val version: Int = 1,
    val detailUrl: String? = null,
    val occurredAt: ZonedDateTime = ZonedDateTime.now(),
    val description: String? = null,
    @JsonSetter(nulls = Nulls.SKIP)
    val additionalInformation: Map<String, Any?> = emptyMap(),
    val personReference: PersonReference = PersonReference()
) {
    data class PersonReference(val identifiers: List<PersonIdentifier> = listOf())
    data class PersonIdentifier(val type: String, val value: String)
}