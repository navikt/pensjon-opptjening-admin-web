package no.nav.pensjon.opptjening.adminweb.external.dto

data class PgiSynkroniseringSettIdRequest(
    val synkroniserFraId: Long,
) {
    companion object {
        val FORSTE = PgiSynkroniseringSettIdRequest(synkroniserFraId = 0L)
    }
}