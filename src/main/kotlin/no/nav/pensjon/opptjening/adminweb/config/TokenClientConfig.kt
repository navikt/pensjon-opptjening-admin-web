package no.nav.pensjon.opptjening.adminweb.config

import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import org.springframework.context.annotation.Configuration

@Configuration
//enables auto-configuration of clients configured with properties under no.nav.security.jwt.client.*
@EnableOAuth2Client(cacheEnabled = true, cacheMaximumSize = 100, cacheEvictSkew = 30)
class TokenClientConfig