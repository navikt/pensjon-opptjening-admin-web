package no.nav.pensjon.opptjening.adminweb.utils

import java.time.ZonedDateTime
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.LoggerFactory

class AuditLogUtils(
    private val tokenValidationContextHolder: TokenValidationContextHolder,
) {
    companion object {
        private val secure = LoggerFactory.getLogger("secure")
        private val audit = LoggerFactory.getLogger("audit")
    }

    fun auditLog(
        operation: Operation,
        fnr: String? = null,
        function: String,
        begrunnelse: String? = null,
        informasjon: String? = null,
        sporingsId: String? = null,
    ) {
        val userId = getNavUserId()
        val cefMessage = createCefMessage(
            fnr = fnr,
            navUserId = userId,
            operation = operation,
            function = function,
            navCallId = sporingsId,
            userProvidedReason = begrunnelse,
            informasjon = informasjon,
        )
        secure.info("AUDIT LOG: $cefMessage")
        audit.info(cefMessage)
    }

    private fun getNavUserId(): String {
        val token = tokenValidationContextHolder.getTokenValidationContext().firstValidToken
        val userName = token?.jwtTokenClaims?.getStringClaim("preferred_username")
        val ident = token?.jwtTokenClaims?.getStringClaim("NAVident")
        secure.info("AdminResource: user = $userName (ident = $ident)")
        return ident ?: "-"
    }

    private fun createCefMessage(
        fnr: String? = null,
        navUserId: String,
        operation: Operation,
        function: String,
        navCallId: String? = null,
        userProvidedReason: String? = null,
        informasjon: String? = null,
    ): String {
        val application = "pensjon-opptjening-admin-web"
        val now = ZonedDateTime.now().toInstant().toEpochMilli()

        val extensions = listOf(
            "end=$now",
            "suid=$navUserId",
            fnr?.let { "duid=$it" },
            "sproc=$function",
            navCallId?.let { "call-id=$navCallId" },
            userProvidedReason?.let {
                val toLongExt = if (userProvidedReason.length > 500) "..." else ""
                val string = userProvidedReason.trim().replace("[^ a-zA-Z0-9_,.-]".toRegex(), "?").take(500)
                "flexString1Label=Reason flexString1=\"$string$toLongExt\""
            },
            informasjon?.let {
                val toLongExt = if (informasjon.length > 500) "..." else ""
                val string = informasjon.trim().replace("[^ a-zA-Z0-9_,.-]".toRegex(), "?").take(500)
                "flexString2Label=Informasjon flexString2=\"$string$toLongExt\""
            },
        )
        val extensionsString = extensions.filterNotNull().joinToString(" ")

        return "CEF:0|Pensjon Opptjening|$application|1.0|${operation.logString}|Sporingslogg|INFO|$extensionsString"
    }

    enum class Operation(val logString: String) {
        CREATE("audit:create"),
        READ("audit:access"),
        WRITE("audit:update"),
        DELETE("audit:delete"),
    }
}