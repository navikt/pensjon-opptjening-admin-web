package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiSynkroniseringResponse
import no.nav.pensjon.opptjening.adminweb.utils.AuditLogUtils
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.apache.coyote.Response
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pgi-synkronisering")
@Protected
class PgiSynkroniseringResource(
    private val poppKlient: PoppKlient,
    tokenValidationContextHolder: TokenValidationContextHolder,
) {
    private val auditLogUtils = AuditLogUtils(tokenValidationContextHolder)

    private val NOT_IMPLEMENTED_RESPONSE = ResponseEntity.status(501)

    companion object {
        private val secure = LoggerFactory.getLogger("secure")
    }

    @GetMapping("/status")
    fun status(): ResponseEntity<PgiSynkroniseringResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.READ,
            fnr = null,
            function = "pgi-synkronisering:hent-status",
            informasjon = "funksjonen er ikke implementert",
        )
        return NOT_IMPLEMENTED_RESPONSE.build()
    }

    @PostMapping("/sett-id-til-forste")
    fun settTilForste(
        @RequestParam("begrunnelse") begrunnelse: String,
    ) : ResponseEntity<PgiSynkroniseringResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            fnr = null,
            function = "pgi-synkronisering:sett-id til forste",
            begrunnelse = begrunnelse,
            informasjon = "funksjonen er ikke implementert",
        )
        return NOT_IMPLEMENTED_RESPONSE.build()
    }

    @PostMapping("/sett-id")
    fun settId(
        @RequestParam("id") id: Long,
        @RequestParam("begrunnelse") begrunnelse: String,
    ) : ResponseEntity<PgiSynkroniseringResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            fnr = null,
            function = "pgi-synkronisering:sett-id",
            begrunnelse = begrunnelse,
            informasjon = "funksjonen er ikke implementert",
        )
        return NOT_IMPLEMENTED_RESPONSE.build()
    }

    @PostMapping("/sett-status")
    fun settStatus(
        @RequestParam("tilstand") tilstand: String,
        @RequestParam("begrunnelse") begrunnelse: String,
    ) : ResponseEntity<PgiSynkroniseringResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            fnr = null,
            function = "pgi-synkronisering:sett-status",
            begrunnelse = begrunnelse,
            informasjon = "funksjonen er ikke implementert",
        )
        return NOT_IMPLEMENTED_RESPONSE.build()
    }
}