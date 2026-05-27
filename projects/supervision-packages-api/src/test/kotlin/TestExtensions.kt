package uk.gov.justice.digital.hmpps.supervision

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor

object TestExtensions {
    fun JwtRequestPostProcessor.roles(vararg roles: String): JwtRequestPostProcessor =
        authorities(roles.map { SimpleGrantedAuthority("ROLE_$it") })
}