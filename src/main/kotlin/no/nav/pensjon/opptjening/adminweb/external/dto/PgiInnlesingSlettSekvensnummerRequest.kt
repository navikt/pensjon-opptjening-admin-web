package no.nav.pensjon.opptjening.adminweb.external.dto

data class PgiInnlesingSlettSekvensnummerRequest(
    // primært her for å unngå at {} er en gyldig request
    val slettSekvensnummer: Boolean = true
)
