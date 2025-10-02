package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingHentRequest
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingHentResponse
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingStatusResponse
import org.apache.hc.core5.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class PoppKlient(
    baseUrl: String,
    oboTokenInterceptor: ClientHttpRequestInterceptor,
    private val restClient: RestClient = RestClient.builder()
        .baseUrl(baseUrl)
        .requestInterceptor(oboTokenInterceptor)
        .build()
) {
    companion object {
        private val log = NAVLog(PoppKlient::class)
    }

    fun bestillBehandling(request: String): String {
        return restClient
            .post()
            .uri("/behandling")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .retrieve()
            .toBodilessEntity().headers.getFirst(HttpHeaders.LOCATION)!!
    }

    fun gjenopptaBehandling(behandlingId: String): String {
        return restClient
            .put()
            .uri("/behandling/gjenoppta")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .body(behandlingId)
            .retrieve()
            .body<String>()!!
    }

    fun hentPgiInnlesingStatus(): PgiInnlesingStatusResponse {
        try {
            return restClient
                .get()
                .uri("/pgi/status")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body<PgiInnlesingStatusResponse>()!!
        } catch (t: Throwable) {
            log.secure.warn("kunne ikke hente status for PGI-innelsing", t)
            throw PoppKlientException("Kunne ikke hente status for PGI-innlesing", t)
        }
    }

    class PoppKlientException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
}