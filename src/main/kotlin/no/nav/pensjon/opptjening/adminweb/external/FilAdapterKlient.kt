package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.external.dto.OverforFilRequest
import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.toJson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
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
}