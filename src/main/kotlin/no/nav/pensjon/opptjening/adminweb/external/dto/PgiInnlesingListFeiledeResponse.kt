package no.nav.popp.web.api.endpoint.pgi.model

data class PgiInnlesingListFeiledeResponse(
    val feilede: List<FeilInformasjon>
) {
    data class FeilInformasjon(
        val fnr: String?,
        val ar: Int?,
        val arsak: String?,
        val status: String,
    )
}