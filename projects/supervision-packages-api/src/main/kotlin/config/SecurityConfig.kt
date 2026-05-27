package uk.gov.justice.digital.hmpps.supervision.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun configure(http: HttpSecurity, jwtAuthenticationConverter: JwtAuthenticationConverter): SecurityFilterChain =
        http
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/health/**").permitAll()
                    .requestMatchers("/info/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs.yaml").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/docs/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter) }
            }
            .csrf { it.disable() }
            .cors { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .build()

    @Bean
    fun jwtGrantedAuthoritiesConverter() = JwtGrantedAuthoritiesConverter().apply {
        setAuthoritiesClaimName("authorities")
        setAuthorityPrefix("ROLE_")
    }

    @Bean
    fun jwtAuthenticationConverter(jwtGrantedAuthoritiesConverter: JwtGrantedAuthoritiesConverter) =
        JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
        }
}
