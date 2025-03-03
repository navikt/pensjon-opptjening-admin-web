package no.nav.pensjon.opptjening.filadapter.web.dto

import java.util.*

data class OverforFilResponse(
    val status: Status,
    val filId: UUID?,
    val melding: String,
) {
    companion object {
        fun ok(filId: UUID): OverforFilResponse = OverforFilResponse(Status.OK, filId, "Fil overført")
        fun feilet(filId: UUID?, msg: String): OverforFilResponse = OverforFilResponse(Status.FEILET, filId, msg)
    }
    enum class Status {
        OK,
        FEILET,
    }
}