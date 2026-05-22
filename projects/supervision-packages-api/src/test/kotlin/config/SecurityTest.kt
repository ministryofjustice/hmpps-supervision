package uk.gov.justice.digital.hmpps.supervision.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.assertj.MockMvcTester
import uk.gov.justice.digital.hmpps.supervision.TestExtensions.roles

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {
    @Autowired
    lateinit var mockMvc: MockMvcTester

    @Test
    fun `no token returns 401`() {
        mockMvc.get().uri("/case/123/supervision-package").assertThat().hasStatus(UNAUTHORIZED)
    }

    @Test
    fun `missing role returns 403`() {
        mockMvc
            .get().uri("/case/123/supervision-package")
            .with(jwt().roles("ANOTHER_ROLE"))
            .assertThat()
            .hasStatus(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `correct role returns 200`() {
        mockMvc
            .get().uri("/case/123/supervision-package")
            .with(jwt().roles("PROBATION_API__SUPERVISION_PACKAGE__READ"))
            .assertThat()
            .hasStatusOk()
    }

    @Test
    fun `no auth required for health`() {
        mockMvc.get().uri("/health").assertThat().hasStatusOk()
    }

    @Test
    fun `no auth required for info`() {
        mockMvc.get().uri("/info").assertThat().hasStatusOk()
    }

    @Test
    fun `no auth required for docs`() {
        mockMvc.get().uri("/swagger-ui/index.html").assertThat().hasStatusOk()
    }
}