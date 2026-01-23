package no.nav.pensjon.opptjening.adminweb.external.dto

data class PgiSynkroniseringSettStatusRequest(
    val status: SynkroniseringIdStatus,
)

enum class SynkroniseringIdStatus {
    AKTIV,
    PAUSET,
}
