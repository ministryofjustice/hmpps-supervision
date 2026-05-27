package uk.gov.justice.digital.hmpps.supervision.model

data class ErrorResponse(
    val status: Int,
    val message: String? = null,
    val fields: List<Field>? = null
) {
    data class Field(
        val type: String?,
        val message: String?,
        val field: String?
    )
}
