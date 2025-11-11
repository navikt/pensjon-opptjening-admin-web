package no.nav.pensjon.opptjening.adminweb.external.dto

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