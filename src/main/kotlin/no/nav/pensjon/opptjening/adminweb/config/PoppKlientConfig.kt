package no.nav.pensjon.opptjening.adminweb.config

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pensjon.opptjening.azure.ad.client.TokenProvider

@Configuration
class PoppKlientConfig(
    @Value("\${POPP_URL}") private val baseUrl: String,
    @Qualifier("poppTokenProvider") private val tokenProvider: TokenProvider,
) {

    @Bean
    fun poppKlient(): PoppKlient {
        return PoppKlient(
            baseUrl = baseUrl,
            nextToken = { tokenProvider.getToken() }
        )
    }
}