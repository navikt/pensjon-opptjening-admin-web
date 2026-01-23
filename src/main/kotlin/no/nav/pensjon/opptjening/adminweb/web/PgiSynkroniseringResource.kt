package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiSynkroniseringResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiSynkroniseringSettIdRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiSynkroniseringSettStatusRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.SynkroniseringIdStatus
import no.nav.pensjon.opptjening.adminweb.utils.AuditLogUtils
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.InvalidParameterException

@RestController
@RequestMapping("/pgi-synkronisering")
@Protected
class PgiSynkroniseringResource(
    private val poppKlient: PoppKlient,
    tokenValidationContextHolder: TokenValidationContextHolder,
) {
    private val auditLogUtils = AuditLogUtils(tokenValidationContextHolder)

    companion object {
        private val secure = LoggerFactory.getLogger("secure")
    }

    @GetMapping("/status")
    fun status(): ResponseEntity<PgiSynkroniseringResponse> {
        try {
            auditLogUtils.auditLog(
                operation = AuditLogUtils.Operation.READ,
                fnr = null,
                function = "pgi-synkronisering:hent-status",
                informasjon = "funksjonen er under utvikling",
            )
            return ResponseEntity.ok().body(poppKlient.hentPgiSynkroniseringStatus())
        } catch (ex: Exception) {
            secure.warn("Feil under henting av pgi-synkronisering-status", ex)
            return ResponseEntity.internalServerError().body(PgiSynkroniseringResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/sett-id-til-forste")
    fun settTilForste(
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiSynkroniseringResponse> {
        try {
            auditLogUtils.auditLog(
                operation = AuditLogUtils.Operation.WRITE,
                fnr = null,
                function = "pgi-synkronisering:sett-id til forste",
                begrunnelse = begrunnelse,
                informasjon = "funksjonen er under utvikling",
            )
            return ResponseEntity.ok().body(poppKlient.settPgiSynkroniseringId(PgiSynkroniseringSettIdRequest.FORSTE))
        } catch (ex: Exception) {
            secure.warn("Kunne ikke sette synkroniserings-id", ex)
            return ResponseEntity.internalServerError().body(PgiSynkroniseringResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/sett-id")
    fun settId(
        @RequestParam("id") id: Long,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiSynkroniseringResponse> {
        try {
            assert(id >= 0L) { "synkroniseringsId må være positiv, men var $id" }
            auditLogUtils.auditLog(
                operation = AuditLogUtils.Operation.WRITE,
                fnr = null,
                function = "pgi-synkronisering:sett-id",
                begrunnelse = begrunnelse,
                informasjon = "funksjonen er under utvikling",
            )
            val status = poppKlient.settPgiSynkroniseringId(
                PgiSynkroniseringSettIdRequest(
                    synkroniserFraId = id,
                )
            )
            return ResponseEntity.ok().body(status)
        } catch (ex: Exception) {
            secure.warn("Kunne ikke sette synkroniserings-id", ex)
            return ResponseEntity.internalServerError().body(PgiSynkroniseringResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/sett-status")
    fun settStatus(
        @RequestParam("tilstand") tilstand: String,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiSynkroniseringResponse> {
        try {
            auditLogUtils.auditLog(
                operation = AuditLogUtils.Operation.WRITE,
                fnr = null,
                function = "pgi-synkronisering:sett-status",
                begrunnelse = begrunnelse,
                informasjon = "funksjonen er under utvikling",
            )
            val status = poppKlient.settPgiSynkroniseringIdStatus(
                PgiSynkroniseringSettStatusRequest(
                    status = when (tilstand) {
                        "aktiv" -> SynkroniseringIdStatus.AKTIV
                        "pauset" -> SynkroniseringIdStatus.PAUSET
                        else -> throw IllegalArgumentException(tilstand)
                    }
                )
            )
            return ResponseEntity.ok().body(status)
        } catch (ex: Exception) {
            secure.warn("Kunne ikke sette synkroniserings-status", ex)
            return ResponseEntity.internalServerError().body(PgiSynkroniseringResponse.Error("Intern feil"))
        }
    }
}