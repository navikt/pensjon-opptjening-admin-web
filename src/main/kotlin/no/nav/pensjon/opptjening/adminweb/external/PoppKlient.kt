package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.external.dto.BehandlingStatusResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingHentRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingSettSekvensnummerRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingSettSekvensnummerStatusRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingSettSekvensnummerTilForsteRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingSlettSekvensnummerRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiSynkroniseringResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiSynkroniseringSettIdRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiSynkroniseringSettStatusRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
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
        private val secure = LoggerFactory.getLogger("secure")
    }

    fun bestillBehandling(request: String): BehandlingStatusResponse.Ok {
        val location = restClient
            .post()
            .uri("/behandling")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .retrieve()
            .toBodilessEntity().headers.getFirst(HttpHeaders.LOCATION)!!
        return BehandlingStatusResponse.Ok(location)
    }

    fun gjenopptaBehandling(behandlingId: String): BehandlingStatusResponse.Ok {
        val response = restClient
            .put()
            .uri("/behandling/gjenoppta")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .body(behandlingId)
            .retrieve()
            .body<String>()!!
        return BehandlingStatusResponse.Ok(response)
    }

    fun hentPgiInnlesingStatus(): PgiInnlesingResponse.Status {
        try {
            return restClient
                .get()
                .uri("/pgi/status")
                .retrieve()
                .body<PgiInnlesingResponse.Status>()!!
        } catch (t: Throwable) {
            secure.warn("kunne ikke hente status for PGI-innelsing", t)
            throw PoppKlientException("Kunne ikke hente status for PGI-innlesing", t)
        }
    }

    fun settSekvensnummer(request: PgiInnlesingSettSekvensnummerRequest): PgiInnlesingResponse.SettSekvensnummer {
        return restClient
            .post()
            .uri("/pgi/sekvensnummer/sett")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .retrieve()
            .body<PgiInnlesingResponse.SettSekvensnummer>()!!
    }

    fun settSekvensnummer(request: PgiInnlesingSettSekvensnummerTilForsteRequest): PgiInnlesingResponse.SettSekvensnummer {
        return restClient
            .post()
            .uri("/pgi/sekvensnummer/sett-forste")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .retrieve()
            .body<PgiInnlesingResponse.SettSekvensnummer>()!!
    }

    fun slettSekvensnummer(request: PgiInnlesingSlettSekvensnummerRequest): PgiInnlesingResponse.SlettSekvensnummer {
        return restClient
            .post()
            .uri("/pgi/sekvensnummer/slett")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .retrieve()
            .body<PgiInnlesingResponse.SlettSekvensnummer>()!!
    }

    fun hentPgiForPersonOgÅr(request: PgiInnlesingHentRequest): PgiInnlesingResponse.`HentPgiForPersonOgÅr` {
        return restClient
            .post()
            .uri("/pgi/hent")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .retrieve()
            .body<PgiInnlesingResponse.`HentPgiForPersonOgÅr`>()!!

    }

    fun hentFeiledePgi(): PgiInnlesingResponse.ListFeilede {
        try {
            return restClient
                .get()
                .uri("/pgi/feilet/list")
                .retrieve()
                .body<PgiInnlesingResponse.ListFeilede>()!!
        } catch (t: Throwable) {
            secure.warn("kunne ikke hente status for PGI-innelsing", t)
            throw PoppKlientException("Kunne ikke hente status for PGI-innlesing", t)
        }
    }

    fun settPgiSekvensnummerStatus(request: PgiInnlesingSettSekvensnummerStatusRequest): PgiInnlesingResponse.Status {
        try {
            return restClient
                .post()
                .uri("/pgi/sekvensnummer/oppdater-status")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body<PgiInnlesingResponse.Status>()!!
        } catch (t: Throwable) {
            secure.warn("kunne ikke sette status for PGI-sekvensnummer", t)
            throw PoppKlientException("kunne ikke sette status for PGI-sekvensnummer", t)
        }
    }

    fun hentPgiSynkroniseringStatus(): PgiSynkroniseringResponse.Status {
        try {
            return restClient
                .get()
                .uri("/pgi-synkronisering/status")
                .retrieve()
                .body<PgiSynkroniseringResponse.Status>()!!
        } catch (t: Throwable) {
            secure.warn("Kunne ikke hente status for PGI-innlesing", t)
            throw PoppKlientException("Kunne ikke hente status for PGI-innlesing", t)
        }
    }

    fun settPgiSynkroniseringId(request: PgiSynkroniseringSettIdRequest): PgiSynkroniseringResponse.Status {
        try {
            return restClient
                .post()
                .uri("/pgi-synkronisering/id/sett-id")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body<PgiSynkroniseringResponse.Status>()!!
        } catch (t: Throwable) {
            secure.warn("kunne ikke sette id for PGI-synkronisering", t)
            throw PoppKlientException("kunne ikke sette id for PGI-synkronisering", t)
        }
    }

    fun settPgiSynkroniseringIdStatus(request: PgiSynkroniseringSettStatusRequest): PgiSynkroniseringResponse.Status {
        try {
            return restClient
                .post()
                .uri("/pgi-synkronisering/id/sett-tilstand")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body<PgiSynkroniseringResponse.Status>()!!
        } catch (t: Throwable) {
            secure.warn("kunne ikke sette tilstand for PGI-synkronisering", t)
            throw PoppKlientException("kunne ikke sette tilstand for PGI-synkronisering", t)
        }
    }

    class PoppKlientException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
}