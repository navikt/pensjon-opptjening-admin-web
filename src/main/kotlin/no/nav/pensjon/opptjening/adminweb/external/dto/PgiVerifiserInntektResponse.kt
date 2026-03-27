package no.nav.popp.web.api.endpoint.pgi.model

sealed class PgiVerifiserInntektResponse {
    data class Ok(
        val fnr: String?,
        val personId: Long?,
        val fodselsar: Int?,
        val rådataId: Long?,
        val rådataInntekter: Map<String, Long>?,
        val inntekter: Map<String, Long>?,
        val resultatType: String?,
    ): PgiVerifiserInntektResponse()

    data class Error(val error: String): PgiVerifiserInntektResponse()
}