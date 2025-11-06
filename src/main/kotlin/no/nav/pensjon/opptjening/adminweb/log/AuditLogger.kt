package no.nav.pensjon.opptjening.adminweb.log

import java.time.ZonedDateTime.now

internal object AuditLogger {

    val log = NAVLog(AuditLogger::class)

    fun createcCefMessage(
        fnr: String?,
        navUserId: String,
        operation: Operation,
        function: String,
    ): String {
        val application = "pensjon-opptjening-admin-web"
        val now = now().toInstant().toEpochMilli()
        val duidStr = fnr?.let { " duid=$it" } ?: ""

//        return "CEF:0|Pensjon Opptjening|$application|0.0|${operation.logString}|Sporingslogg|INFO|end=$now$duidStr" +
//            " suid=$navUserId request=$requestPath flexString1Label=Decision flexString1=$permit"
        val cefMessage = "CEF:0|Pensjon Opptjening|$application|null|${operation.logString}|Sporingslogg|INFO|end=$now$duidStr" +
                " suid=$navUserId request=$function"
        log.secure.info("cefMessage=$cefMessage")
        return cefMessage
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
