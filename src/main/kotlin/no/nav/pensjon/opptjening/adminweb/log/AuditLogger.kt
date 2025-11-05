package no.nav.pensjon.opptjening.adminweb.log

import java.time.ZonedDateTime.now

internal class AuditLogger {
    fun createcCefMessage(
        fnr: String?,
        navEmail: String,
        operation: Operation,
        requestPath: String,
        permit: Permit,
    ): String {
        val application = "pensjon-opptjening-admin-web"
        val now = now().toInstant().toEpochMilli()
        val subject = fnr?.padStart(11, '0')
        val duidStr = subject?.let { " duid=$it" } ?: ""

        return "CEF:0|Pensjon Opptjening|$application|version is not included|${operation.logString}|Sporingslogg|INFO|end=$now$duidStr" +
            " suid=$navEmail request=$requestPath flexString1Label=Decision flexString1=$permit"
    }

    enum class Operation(val logString: String) {
        READ("audit:access"),
        WRITE("audit:update"),
        UNKNOWN("audit:unknown"),
    }

    enum class Permit(val logString: String) {
        PERMIT("Permit"),
        DENY("Deny"),
    }
}
