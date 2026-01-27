package no.nav.pensjon.opptjening.adminweb.external.dto

sealed class PgiSynkroniseringResponse {
    data class Status(
        val syncId: PgiSyncIdStatus
    ) : PgiSynkroniseringResponse() {
        data class PgiSyncIdStatus(
            val sekvensnummer: Long?,
            val aktiv: Boolean?,
        )

        companion object {
            val EMPTY = Status(PgiSyncIdStatus(null, null))
            fun of(aktiv: Boolean, syncId: Long): Status {
                return Status(
                    PgiSyncIdStatus(
                        sekvensnummer = syncId,
                        aktiv = aktiv,
                    )
                )
            }
        }
    }

    data class Error(
        val message: String
    ) : PgiSynkroniseringResponse() {
    }
}
