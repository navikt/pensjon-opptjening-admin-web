package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Protected
class AdminWebApi(
    private val filAdapterKlient: FilAdapterKlient,
) {
    companion object {
        private val log = NAVLog(AdminWebApi::class)
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

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        log.open.info("Sa hei")
        return ResponseEntity.ok("pong")
    }

}