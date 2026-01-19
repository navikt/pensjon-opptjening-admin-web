package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.external.dto.BehandlingStatusResponse
import no.nav.pensjon.opptjening.adminweb.utils.AuditLogUtils
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/behandling")
@Protected
class BehandlingResource(
    private val poppKlient: PoppKlient,
    tokenValidationContextHolder: TokenValidationContextHolder,
) {
    private val auditLogUtils = AuditLogUtils(tokenValidationContextHolder)

    companion object {
        private val secure = LoggerFactory.getLogger("secure")
    }

    @PostMapping("/bestill")
    fun behandling(
        @RequestParam("request") request: String,
        @RequestParam("begrunnelse") begrunnelse: String?,
    ): ResponseEntity<BehandlingStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            function = "bestill behandling",
            informasjon = request,
            begrunnelse = begrunnelse,
        )
        return try {
            ResponseEntity.ok(poppKlient.bestillBehandling(request))
        } catch (e: Throwable) {
            secure.warn("Feil ved bestilling av behandling: ${e.message}", e)
            ResponseEntity.internalServerError().body(BehandlingStatusResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/gjenoppta")
    fun rekjørBehandling(
        @RequestParam("behandlingId") behandlingId: String,
        @RequestParam("begrunnelse") begrunnelse: String?,
    ): ResponseEntity<BehandlingStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            function = "bestill behandling",
            informasjon = behandlingId,
            begrunnelse = begrunnelse,
        )
        return try {
            ResponseEntity.ok(poppKlient.gjenopptaBehandling(behandlingId))
        } catch (e: Throwable) {
            secure.warn("Feil ved rekjøring av behandling: ${e.message}", e)
            ResponseEntity.internalServerError().body(BehandlingStatusResponse.Error("Intern feil"))
        }
    }
}