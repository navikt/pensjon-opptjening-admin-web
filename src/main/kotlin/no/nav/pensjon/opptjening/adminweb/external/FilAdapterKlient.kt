package no.nav.pensjon.opptjening.adminweb.external

import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import no.nav.pensjon.opptjening.adminweb.external.dto.FilStatusResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.OverforFilRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

class FilAdapterKlient(
    baseUrl: String,
    oboTokenInterceptor: ClientHttpRequestInterceptor,
    private val restClient: RestClient = RestClient.builder()
        .baseUrl(baseUrl)
        .requestFactory(HttpComponentsClientHttpRequestFactory().also {
            it.setReadTimeout(180.seconds.toJavaDuration())
        })
        .requestInterceptor(oboTokenInterceptor)
        .build()
) {

    fun listFiler(): FilStatusResponse.ListOk {
        return try {
            restClient
                .get()
                .uri("/list")
                .retrieve()
                .body<FilStatusResponse.ListOk>()!!
        } catch (e: Throwable) {
            throw FilAdapterException("Feil ved kall til filadapter", e)
        }
    }

    fun overførFil(filnavn: String): FilStatusResponse.OverforOk {
        return try {
            val response = restClient
                .post()
                .uri("/overfor")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(OverforFilRequest(filnavn))
                .retrieve()
                .body<String>()!!
            FilStatusResponse.OverforOk(response)
        } catch (e: Throwable) {
            throw FilAdapterException("Feil ved kall til filadapter", e)
        }
    }

    class FilAdapterException(msg: String, e: Throwable?) : RuntimeException(msg, e)
}