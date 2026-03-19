package no.nav.pensjon.opptjening.adminweb.external.dto

import java.time.LocalDateTime

data class LeggTilFoedselsaarRequest(
    val personId: Long,
    val foedselsaar: Int,
    val master: String,
    val opprettetRegistrert: LocalDateTime,
    val opprettetSystemKilde: String,
    val opprettetKilde: String,
    val referanse: String,
)
