package uk.gov.justice.digital.hmpps.supervision.utils

import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Retry {
    fun <T> retry(
        maxRetries: Int,
        exceptions: List<KClass<out Exception>> = listOf(Exception::class),
        delay: Duration = 100.milliseconds,
        code: () -> T
    ): T {
        for (attempt in 1..maxRetries) {
            try {
                return code()
            } catch (e: Throwable) {
                if (attempt == maxRetries || exceptions.none { it.isInstance(e) }) throw e
                Thread.sleep((delay * attempt * attempt).inWholeMilliseconds)
            }
        }
        error("Unexpected retry failure")
    }
}
