package no.nav.pensjon.opptjening.adminweb.config

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration

@Configuration
@EnableJwtTokenValidation
class TokenValidationConfig