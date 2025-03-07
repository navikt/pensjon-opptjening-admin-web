package no.nav.pensjon.opptjening.adminweb.external

import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PoppKlient(
    private val baseUrl: String,
    private val nextToken: () -> String,
) {
    private val client = OkHttpClient.Builder().build()

    companion object {
        private val log = NAVLog(PoppKlient::class)
    }

    fun bestillBehandling(request: String): String {
        val request = Request.Builder()
            .post(request.toRequestBody())
            .url("$baseUrl/behandling")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .addHeader("Content-Type", "application/json")
            .build()

        return client.newCall(request).execute().use { response -> response.body!!.string() }
    }

    fun gjenopptaBehandling(behandlingId: String): String {
        val request = Request.Builder()
            .post(behandlingId.toRequestBody())
            .url("$baseUrl/behandling/gjenoppta")
            .addHeader("Authorization", "Bearer ${nextToken()}")
            .addHeader("Content-Type", "application/json")
            .build()

        return client.newCall(request).execute().use { response -> response.body!!.string() }
    }
}