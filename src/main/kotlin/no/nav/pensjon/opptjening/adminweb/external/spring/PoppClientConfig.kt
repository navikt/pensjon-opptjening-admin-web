package no.nav.pensjon.opptjening.adminweb.external.spring

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pensjon.opptjening.azure.ad.client.TokenProvider

@Configuration
class PoppClientConfig(
    @Value("\${FILADAPTER_URL}") private val baseUrl: String,
    private val poppTokenProvider: TokenProvider,
) {

    @Bean
    fun filAdapterKlient(): FilAdapterKlient {
        return FilAdapterKlient(
            baseUrl = "baseUrl",
            nextToken = { poppTokenProvider.getToken() }
        )
    }
}