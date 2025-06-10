package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.external.dto.ListFilerResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.OverforFilRequest
import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.mapToObject
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.toJson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class FilAdapterKlient(
    private val baseUrl: String,
    private val nextToken: () -> String,
) {
    companion object {
        private val log = NAVLog(FilAdapterKlient::class)
    }

    fun listFiler(): ListFilerResponse {
        log.open.info("listFiler: baseUrl=$baseUrl")

        val request = Request.Builder()
            .get()
            .url("$baseUrl/list")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .build()

        val client = OkHttpClient.Builder()
            .readTimeout(180, TimeUnit.SECONDS)
            .build()
        val responseBody = client.newCall(request).execute().use { response -> response.body!!.string() }
        return try {
            responseBody.mapToObject(ListFilerResponse::class.java)
        } catch (e: Throwable) {
            log.secure.error("List filer: kan ikke parse response fra filadapter: $responseBody", e)
            throw FilAdapterException("Kan ikke parse response fra filadapter", e)
        }
    }

    fun overførFil(filnavn: String): String {
        log.open.info("overførFil: baseUrl=$baseUrl filnavn=$filnavn")

        val request = Request.Builder()
            .post(OverforFilRequest(filnavn).toJson().toRequestBody("application/json".toMediaTypeOrNull()))
            .url("$baseUrl/overfor")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .build()

        val client = OkHttpClient.Builder()
            .readTimeout(180, TimeUnit.SECONDS)
            .build()
        return client.newCall(request).execute().use { response -> response.body!!.string() }
    }

    class FilAdapterException(msg: String, e: Throwable?) : RuntimeException(msg, e)
}