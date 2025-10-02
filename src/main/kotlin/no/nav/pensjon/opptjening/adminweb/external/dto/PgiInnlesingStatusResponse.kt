package no.nav.popp.web.api.endpoint.pgi.model

data class PgiInnlesingStatusResponse(
    val sekvensnummer: PgiSekvensnummerStatus
) {
    data class PgiSekvensnummerStatus(
        val sekvensnummer: Long?,
    )
}