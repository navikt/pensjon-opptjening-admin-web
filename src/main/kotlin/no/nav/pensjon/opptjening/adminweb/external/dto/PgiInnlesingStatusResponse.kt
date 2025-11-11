package no.nav.pensjon.opptjening.adminweb.external.dto

data class PgiInnlesingStatusResponse(
    val sekvensnummer: PgiSekvensnummerStatus
) {
    data class PgiSekvensnummerStatus(
        val sekvensnummer: Long?,
    )
}