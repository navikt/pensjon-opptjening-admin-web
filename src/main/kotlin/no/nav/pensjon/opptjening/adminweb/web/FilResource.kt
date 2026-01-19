package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.external.dto.FilStatusResponse
import no.nav.pensjon.opptjening.adminweb.utils.AuditLogUtils
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fil")
@Protected
class FilResource(
    private val filAdapterKlient: FilAdapterKlient,
    tokenValidationContextHolder: TokenValidationContextHolder,
) {
    private val auditLogUtils = AuditLogUtils(tokenValidationContextHolder)

    companion object {
        private val secure = LoggerFactory.getLogger("secure")
    }

    @GetMapping("/list", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun listFiler(): ResponseEntity<String> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.READ,
            function = "list filer",
        )
        return try {
            val filer = filAdapterKlient.listFiler()
            val body = filer.filer.joinToString("\n") {
                val lagret = if (it.lagretMedId != null) " lagret med id ${it.lagretMedId}" else ""
                val pågående = if (it.lagresMedId.isNotEmpty()) " under lagring: ${it.lagresMedId}" else ""
                "${it.filnavn}[${it.size}$lagret$pågående]"
            }
            ResponseEntity.ok(body)
        } catch (e: FilAdapterKlient.FilAdapterException) {
            secure.error("Feil ved listing av filer", e)
            ResponseEntity.internalServerError().body("Feil i kommunikasjon med filadapter")
        } catch (e: Throwable) {
            secure.warn("Feil ved listing av filer: ${e.message}", e)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }

    @PostMapping("/overfor")
    fun overforFil(
        @RequestParam("filnavn") filnavn: String,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<FilStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            function = "overfør fil",
            begrunnelse = begrunnelse,
            informasjon = filnavn,
        )
        return try {
            ResponseEntity.ok(filAdapterKlient.overførFil(filnavn))
        } catch (e: Throwable) {
            secure.warn("Feil ved overføring av fil: ${e.message}", e)
            ResponseEntity.internalServerError().body(FilStatusResponse.Error("Intern feil"))
        }
    }
}