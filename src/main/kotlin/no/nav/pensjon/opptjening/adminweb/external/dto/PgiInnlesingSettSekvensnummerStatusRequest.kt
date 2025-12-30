package no.nav.pensjon.opptjening.adminweb.external.dto

data class PgiInnlesingSettSekvensnummerStatusRequest(
    val status: SekvensnummerStatus,
)

enum class SekvensnummerStatus {
    AKTIV,
    PAUSET,
}
