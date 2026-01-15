package no.nav.pensjon.opptjening.adminweb.external.dto

sealed class PgiStatusResponse {
    data class StatusOk(
        val sekvensnummer: PgiSekvensnummerStatus
    ) : PgiStatusResponse() {
        data class PgiSekvensnummerStatus(
            val sekvensnummer: Long?,
            val aktiv: Boolean?,
        )
    }

    data class SettSekvensnummerOk(
        val success: Boolean,
        val sekvensnummer: Long?,
    ) : PgiStatusResponse()

    data class SlettSekvensnummerOk(
        val sekvensnummerHarBlittSlettet: Boolean,
        val tidligereSekvensnummer: Long?,
    ) : PgiStatusResponse()

    data class HentPgiForPersonOgÅrOk(
        val success: Boolean,
        val debug: String,
    ) : PgiStatusResponse()

    data class ListFeiledeOk(
        val feilede: List<FeilInformasjon>
    ) : PgiStatusResponse() {
        data class FeilInformasjon(
            val fnr: String?,
            val ar: Int?,
            val arsak: String?,
            val status: String,
        )
    }

    data class Error(
        val message: String
    ) : PgiStatusResponse()
}
