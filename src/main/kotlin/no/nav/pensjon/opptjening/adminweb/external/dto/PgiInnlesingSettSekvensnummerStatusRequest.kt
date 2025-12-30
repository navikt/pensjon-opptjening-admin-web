package no.nav.pensjon.opptjening.adminweb.external.dto

data class PgiInnlesingSettSekvensnummerStatusRequest(
    private val status: SekvensnummerStatus,
)

enum class SekvensnummerStatus {
    AKTIV,
    PAUSET,
}
