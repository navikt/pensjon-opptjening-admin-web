package no.nav.pensjon.opptjening.adminweb.external.spring

import no.nav.pensjon.opptjening.adminweb.external.PoppClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
internal class PoppClientConfig(
    @Value("\${POPP_URL}") private val baseUrl: String,
) {
    private val poppClient = PoppClient(
        baseUrl = baseUrl,
    )
}