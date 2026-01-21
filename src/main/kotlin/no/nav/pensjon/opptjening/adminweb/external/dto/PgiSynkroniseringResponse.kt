package no.nav.pensjon.opptjening.adminweb.external.dto

sealed class PgiSynkroniseringResponse {
    data class Status(
        val sekvensnummer: PgiSekvensnummerStatus
    ) : PgiSynkroniseringResponse() {
        data class PgiSekvensnummerStatus(
            val sekvensnummer: Long?,
            val aktiv: Boolean?,
        )
    }

    data class SettId(
        val success: Boolean,
        val sekvensnummer: Long?,
    ) : PgiSynkroniseringResponse()

    data class Error(
        val message: String
    ) : PgiSynkroniseringResponse()
}
