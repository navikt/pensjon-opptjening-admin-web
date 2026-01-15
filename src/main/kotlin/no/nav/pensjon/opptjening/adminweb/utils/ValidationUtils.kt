package no.nav.pensjon.opptjening.adminweb.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ValidationUtils {
    fun gyldigIsoDato(dato: String): Boolean {
        return dato.matches("^\\d{4}-\\d{2}-\\d{2}$".toRegex())
    }

    fun parseIsoDato(dato: String): LocalDate {
        return LocalDate.parse(dato, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun gyldigFnrInput(fnr: String): Boolean {
        return fnr.matches("^[0-9]*$".toRegex())
    }
}