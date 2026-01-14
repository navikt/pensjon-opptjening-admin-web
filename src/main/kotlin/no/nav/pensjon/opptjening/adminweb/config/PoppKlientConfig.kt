package no.nav.pensjon.opptjening.adminweb.config

import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PoppKlientConfig(
    @param:Value("\${POPP_URL}") private val baseUrl: String,
    private val tokenInterceptor: OAuth2ClientRequestInterceptor,
) {

    @Bean
    fun poppKlient(): PoppKlient {
        return PoppKlient(
            baseUrl = baseUrl,
            oboTokenInterceptor = tokenInterceptor
        )
    }
}