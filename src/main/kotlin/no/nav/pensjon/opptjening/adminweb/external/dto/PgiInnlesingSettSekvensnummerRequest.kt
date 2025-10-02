package no.nav.popp.web.api.endpoint.pgi.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PgiInnlesingSettSekvensnummerRequest(
    val dato: String,
) {
    constructor(dato: LocalDate) : this(dato.format(DateTimeFormatter.ISO_LOCAL_DATE)) // yyyy-mm-dd
    fun toLocalDate() : LocalDate = LocalDate.parse(dato, DateTimeFormatter.ISO_LOCAL_DATE)
}