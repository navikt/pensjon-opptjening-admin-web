package no.nav.pensjon.opptjening.adminweb.external.dto

data class PgiInnlesingSettSekvensnummerResponse(
    val success: Boolean,
    val sekvensnummer: Long?,
) {
    constructor(sekvensnummer: Long) : this(true, sekvensnummer)

    companion object {
        val failed = PgiInnlesingSettSekvensnummerResponse(false, null)
    }
}