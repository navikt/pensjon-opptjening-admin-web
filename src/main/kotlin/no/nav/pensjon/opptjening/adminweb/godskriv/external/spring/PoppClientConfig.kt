package no.nav.pensjon.opptjening.adminweb.godskriv.external.spring

import no.nav.pensjon.opptjening.adminweb.godskriv.external.PoppClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import pensjon.opptjening.azure.ad.client.TokenProvider

@Configuration
internal class PoppClientConfig(
    @Value("\${POPP_URL}") private val baseUrl: String,
    @Qualifier("poppTokenProvider") private val tokenProvider: TokenProvider,
) {
    private val poppClient = PoppClient(
        baseUrl = baseUrl,
        tokenProvider = tokenProvider,
    )
}