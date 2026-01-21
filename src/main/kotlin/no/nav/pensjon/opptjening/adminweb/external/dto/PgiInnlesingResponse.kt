package no.nav.pensjon.opptjening.adminweb.external.dto

sealed class PgiInnlesingResponse {
    data class Status(
        val sekvensnummer: PgiSekvensnummerStatus
    ) : PgiInnlesingResponse() {
        data class PgiSekvensnummerStatus(
            val sekvensnummer: Long?,
            val aktiv: Boolean?,
        )
    }

    data class SettSekvensnummer(
        val success: Boolean,
        val sekvensnummer: Long?,
    ) : PgiInnlesingResponse()

    data class SlettSekvensnummer(
        val sekvensnummerHarBlittSlettet: Boolean,
        val tidligereSekvensnummer: Long?,
    ) : PgiInnlesingResponse()

    data class HentPgiForPersonOgÅr(
        val success: Boolean,
        val debug: String,
    ) : PgiInnlesingResponse()

    data class ListFeilede(
        val feilede: List<FeilInformasjon>
    ) : PgiInnlesingResponse() {
        data class FeilInformasjon(
            val fnr: String?,
            val ar: Int?,
            val arsak: String?,
            val status: String,
        )
    }

    data class Error(
        val message: String
    ) : PgiInnlesingResponse()
}
