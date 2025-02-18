package no.nav.pensjon.opptjening.adminweb.external.spring

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pensjon.opptjening.azure.ad.client.TokenProvider

@Configuration
internal class PoppClientConfig(
    private val poppTokenProvider: TokenProvider,
) {

    @Bean
    open fun filAdapterKlient(): FilAdapterKlient {
        return FilAdapterKlient(
            baseUrl = "http://banan",
            nextToken = { poppTokenProvider.getToken() }
        )
    }
}