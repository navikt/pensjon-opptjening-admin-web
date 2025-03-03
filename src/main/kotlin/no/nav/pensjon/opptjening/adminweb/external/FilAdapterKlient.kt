package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.mapToObject
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.toJson
import no.nav.pensjon.opptjening.filadapter.web.dto.OverforFilRequest
import no.nav.pensjon.opptjening.filadapter.web.dto.OverforFilResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class FilAdapterKlient(
    private val baseUrl: String,
    private val nextToken: () -> String,
) {
    companion object {
        private val log = NAVLog(FilAdapterKlient::class)
    }

    fun listFiler(): String {
        log.open.info("listFiler: baseUrl=$baseUrl")

        val request = Request.Builder()
            .get()
            .url("$baseUrl/list")
//            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .build()

        val client = OkHttpClient.Builder().build()
        return client.newCall(request).execute().use { response -> response.body!!.string() }
    }

    fun overførFil(filnavn: String): String {
        log.open.info("overførFil: baseUrl=$baseUrl filnavn=$filnavn")

        val request = Request.Builder()
            .post(OverforFilRequest(filnavn).toJson().toRequestBody("application/json".toMediaTypeOrNull()))
            .url("$baseUrl/overfor")
//            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .build()

        val client = OkHttpClient.Builder().build()
        return client.newCall(request).execute().use { response -> response.body!!.string() }
    }


    private fun buildRequest(url: String, body: Any): Request {
        return Request.Builder()
            .post(body.toJson().toRequestBody("application/json".toMediaTypeOrNull()!!))
            .url(url)
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .build()
    }


    private fun <T : Any> callPopp(
        url: String,
        body: Any,
        responseType: Class<T>
    ): T {
        val client = OkHttpClient.Builder()
            .build()
        val request = buildRequest(
            url = url,
            body = body,
        )
        return client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            if (body.isNullOrEmpty()) {
                throw RuntimeException("No response body, response code is ${response.code}")
            }
            val callResponse =
                try {
                    body.mapToObject(responseType)
                } catch (t: Throwable) {
                    log.open.error("Kunne ikke lese svar fra popp")
                    log.secure.error("Kunne ikke lese svar fra popp", t)
                    log.secure.error("response ${response.code} ${response.message}")
                    log.secure.error("body: $body")
                    throw RuntimeException("Kunne ikke lese svar fra popp", t)
                }
            if (response.code != 200) {
                throw RuntimeException("response status code ${response.code}: $callResponse")
            }
            callResponse
        }
    }
}