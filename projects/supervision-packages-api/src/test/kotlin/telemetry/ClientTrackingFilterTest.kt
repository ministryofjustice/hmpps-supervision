package uk.gov.justice.digital.hmpps.supervision.telemetry

import io.opentelemetry.api.trace.Span
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.assertj.MockMvcTester

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension::class)
class ClientTrackingFilterTest {
    @Autowired
    lateinit var mockMvc: MockMvcTester

    @Test
    fun `should set clientId attribute on current span`() {
        mockStatic(Span::class.java).use { static ->
            val span = mock(Span::class.java)
            static.`when`<Span> { Span.current() }.thenReturn(span)

            mockMvc
                .get().uri("/test")
                .with(jwt().jwt { it.claim("client_id", "test-client-id") })
                .assertThat()
                .hasStatus(NOT_FOUND)

            verify(span).setAttribute("clientId", "test-client-id")
        }
    }
}
