package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.external.dto.LeggTilFoedselsaarRequest
import no.nav.pensjon.opptjening.adminweb.utils.AuditLogUtils
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/person")
@Protected
class PersonResource(
    private val poppKlient: PoppKlient,
    tokenValidationContextHolder: TokenValidationContextHolder,
) {
    private val auditLogUtils = AuditLogUtils(tokenValidationContextHolder)

    companion object {
        private val secure = LoggerFactory.getLogger("secure")
    }

    @PostMapping("/foedselsaar")
    fun leggTilFoedselsaar(
        @RequestParam("personId") personId: Long,
        @RequestParam("foedselsaar") foedselsaar: Int,
        @RequestParam("master") master: String,
        @RequestParam("opprettetRegistrert") opprettetRegistrert: String,
        @RequestParam("opprettetSystemKilde") opprettetSystemKilde: String,
        @RequestParam("opprettetKilde") opprettetKilde: String,
        @RequestParam("referanse") referanse: String,
        @RequestParam("begrunnelse") begrunnelse: String?,
    ): ResponseEntity<Any> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.CREATE,
            function = "opprett fødselsår",
            begrunnelse = begrunnelse,
        )
        return try {
            val request = LeggTilFoedselsaarRequest(
                personId = personId,
                foedselsaar = foedselsaar,
                master = master,
                opprettetRegistrert = LocalDateTime.parse(opprettetRegistrert, DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                opprettetSystemKilde = opprettetSystemKilde,
                opprettetKilde = opprettetKilde,
                referanse = referanse,
            )
            ResponseEntity.ok(poppKlient.leggTilFoedselsaar(request))
        } catch (e: Throwable) {
            secure.warn("Feil ved opprettelse av fødselsår: ${e.message}", e)
            ResponseEntity.internalServerError().body(e)
        }
    }

    @PostMapping("/foedselsaar/slett")
    fun slettFoedselsaar(
        @RequestParam("personId") personId: Long,
        @RequestParam("foedselsaarId") foeselsaarId: Long,
        @RequestParam("begrunnelse") begrunnelse: String?,
    ): ResponseEntity<Any> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.DELETE,
            function = "slett fødselsår",
            begrunnelse = begrunnelse,
        )
        return try {
            ResponseEntity.ok(poppKlient.slettFoedselsaar(personId, foeselsaarId))
        } catch (e: Throwable) {
            secure.warn("Feil ved sletting av fødselsår: ${e.message}", e)
            ResponseEntity.internalServerError().body(e)
        }
    }
}
