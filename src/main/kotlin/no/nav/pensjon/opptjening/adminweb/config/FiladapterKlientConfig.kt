package no.nav.pensjon.opptjening.adminweb.config

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.security.token.support.client.spring.oauth2.OAuth2ClientRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FiladapterKlientConfig(
    @Value("\${FILADAPTER_URL}") private val baseUrl: String,
    @Autowired private val tokenInterceptor: OAuth2ClientRequestInterceptor,
) {

    @Bean
    fun filAdapterKlient(): FilAdapterKlient {
        return FilAdapterKlient(
            baseUrl = baseUrl,
            oboTokenInterceptor = tokenInterceptor
        )
    }
}