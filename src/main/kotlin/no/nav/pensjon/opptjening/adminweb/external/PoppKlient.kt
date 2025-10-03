package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingHentRequest
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingHentResponse
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingListFeiledeResponse
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingStatusResponse
import org.apache.hc.core5.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
                .retrieve()
                .body<PgiInnlesingStatusResponse>()!!
        } catch (t: Throwable) {
            log.secure.warn("kunne ikke hente status for PGI-innelsing", t)
            throw PoppKlientException("Kunne ikke hente status for PGI-innlesing", t)
        }
    }

    fun hentPgiForPersonOgÅr(request: PgiInnlesingHentRequest): PgiInnlesingHentResponse {
        return restClient
            .post()
            .uri("/pgi/hent")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .retrieve()
            .body<PgiInnlesingHentResponse>()!!

    }

    fun hentFeiledePgi(): PgiInnlesingListFeiledeResponse {
        try {
            return restClient
                .get()
                .uri("/pgi/status")
                .retrieve()
                .body<PgiInnlesingListFeiledeResponse>()!!
        } catch (t: Throwable) {
            log.secure.warn("kunne ikke hente status for PGI-innelsing", t)
            throw PoppKlientException("Kunne ikke hente status for PGI-innlesing", t)
        }
    }

    @PostMapping(
        path = ["/feilet/rekjor"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    // TODO: Tilby senere
    fun rekjorFeilede() {
        // /feilet/rekjor
        throw NotImplementedError("Ikke implementert enda")
    }

    class PoppKlientException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
}