package uk.gov.justice.digital.hmpps.supervision.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.assertj.MockMvcTester
import uk.gov.justice.digital.hmpps.supervision.TestExtensions.roles

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvcTester

    @Test
    fun `calls the api`() {
        mockMvc
            .get().uri("/case/123/supervision-package")
            .with(jwt().roles("PROBATION_API__SUPERVISION_PACKAGE__READ"))
            .assertThat()
            .hasStatusOk()
            .body().isEmpty()
    }
}