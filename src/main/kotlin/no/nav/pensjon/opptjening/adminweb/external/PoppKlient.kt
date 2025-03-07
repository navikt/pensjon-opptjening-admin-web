package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.external.dto.OverforFilRequest
import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.toJson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PoppKlient(
    private val baseUrl: String,
    private val nextToken: () -> String,
) {
    companion object {
        private val log = NAVLog(PoppKlient::class)
        val client = OkHttpClient.Builder().build()
    }

    fun bestillBehandling(): String {
        log.open.info("listFiler: baseUrl=$baseUrl")

        val request = Request.Builder()
            .get()
            .url("$baseUrl/api/behandling")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .build()


        return client.newCall(request).execute().use { response -> response.body!!.string() }
    }
}