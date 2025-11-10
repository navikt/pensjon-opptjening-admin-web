package no.nav.pensjon.opptjening.adminweb.log

import java.time.ZonedDateTime.now

internal object AuditLogger {

    val log = NAVLog(AuditLogger::class)

    fun createcCefMessage(
        fnr: String? = null,
        navUserId: String,
        operation: Operation,
        function: String,
        navCallId: String? = null,
        userProvidedReason: String? = null,
        informasjon: String? = null,
    ): String {
        val application = "pensjon-opptjening-admin-web"
        val now = now().toInstant().toEpochMilli()

        val extensions = listOf(
            "end=$now",
            "suid=$navUserId",
            fnr?.let { "duid=$it" },
            "sproc=$function",
            navCallId?.let { "call-id=$navCallId" },
            userProvidedReason?.let {
                val toLongExt = if (userProvidedReason.length > 500) "..." else ""
                val string = userProvidedReason.trim().replace("[^a-zA-Z0-9_,.-]".toRegex(), "?").take(500)
                "flexString1Label=Reason flexString1=\"$string$toLongExt\""
            },
            informasjon?.let {
                val toLongExt = if (informasjon.length > 500) "..." else ""
                val string = informasjon.trim().replace("[^a-zA-Z0-9_,.-]".toRegex(), "?").take(500)
                "flexString2Label=Informasjon flexString2=\"$string$toLongExt\""
            },
        )
        val extensionsString = extensions.filterNotNull().joinToString(" ")

        val cefMessage =
            "CEF:0|Pensjon Opptjening|$application|1.0|${operation.logString}|Sporingslogg|INFO|$extensionsString"

        log.secure.info("cefMessage=$cefMessage")
        return cefMessage
    }

    enum class Operation(val logString: String) {
        CREATE("audit:create"),
        READ("audit:access"),
        WRITE("audit:update"),
        DELETE("audit:delete"),
    }

    enum class Permit(val logString: String) {
        PERMIT("Permit"),
        DENY("Deny"),
    }
}
