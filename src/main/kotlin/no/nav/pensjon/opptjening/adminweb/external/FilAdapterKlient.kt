package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.external.dto.ListFilerResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.OverforFilRequest
import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.toJson
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

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
    companion object {
        private val log = NAVLog(FilAdapterKlient::class)
    }

    fun listFiler(): ListFilerResponse {
        return try {
            restClient
                .get()
                .uri("/list")
                .retrieve()
                .body<ListFilerResponse>()!!
        } catch (e: Throwable) {
            throw FilAdapterException("Feil ved kall til filadapter", e)
        }
    }

    fun overførFil(filnavn: String): String {
        return try {
            restClient
                .post()
                .uri("/overfor")
                .body(OverforFilRequest(filnavn).toJson())
                .retrieve()
                .body<String>()!!
        } catch (e: Throwable) {
            throw FilAdapterException("Feil ved kall til filadapter", e)
        }
    }

    class FilAdapterException(msg: String, e: Throwable?) : RuntimeException(msg, e)
}