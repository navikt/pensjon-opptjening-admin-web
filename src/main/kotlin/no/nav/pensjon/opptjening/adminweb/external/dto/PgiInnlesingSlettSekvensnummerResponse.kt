package no.nav.pensjon.opptjening.adminweb.external.dto

data class PgiInnlesingSlettSekvensnummerResponse(
    val sekvensnummerHarBlittSlettet: Boolean,
    val tidligereSekvensnummer: Long?,
)
