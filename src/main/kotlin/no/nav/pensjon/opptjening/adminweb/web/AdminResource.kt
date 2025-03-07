package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Protected
class AdminResource(
    private val filAdapterKlient: FilAdapterKlient,
    private val poppKlient: PoppKlient,
) {
    companion object {
        private val log = NAVLog(AdminResource::class)
    }

    @GetMapping("/list")
    fun listFiler(): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(filAdapterKlient.listFiler())
        } catch (e: Throwable) {
            log.open.warn("Feil ved listing av filer: ${e.message}")
            log.secure.warn("Feil ved listing av filer: ${e.message}", e)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }

    @PostMapping("/overfor")
    fun overforFil(
        @RequestParam("filnavn") filnavn: String,
    ): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(filAdapterKlient.overførFil(filnavn))
        } catch (e: Throwable) {
            log.open.warn("Feil ved overføring av fil: ${e.message}")
            log.secure.warn("Feil ved overføring av fil: ${e.message}", e)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }

    @PostMapping("/behandling")
    fun behandling(
        @RequestParam("request") request: String,
    ): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(poppKlient.bestillBehandling(request))
        } catch (e: Throwable) {
            log.open.warn("Feil ved bestilling av behandling: ${e.message}")
            log.secure.warn("Feil ved bestilling av behandling: ${e.message}", e)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }

    @PostMapping("/behandling/rekjor")
    fun rekjørBehandling(
        @RequestParam("behandlingId") behandlingId: String,
    ): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(poppKlient.gjenopptaBehandling(behandlingId))
        } catch (e: Throwable) {
            log.open.warn("Feil ved rekjøring av behandling: ${e.message}")
            log.secure.warn("Feil ved rekjøring av behandling: ${e.message}", e)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        log.open.info("Sa hei")
        return ResponseEntity.ok("pong")
    }
}