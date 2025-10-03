package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.toJson
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingHentRequest
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.MediaType
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

    @GetMapping("/list", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun listFiler(): ResponseEntity<String> {
        return try {
            val filer = filAdapterKlient.listFiler()
            val body = filer.filer.joinToString("\n") {
                val lagret = if (it.lagret) " lagret" else ""
                "${it.filnavn}[${it.size}$lagret]"
            }
            ResponseEntity.ok(body)
        } catch (e: FilAdapterKlient.FilAdapterException) {
            log.secure.error("Feil ved listing av filer", e)
            ResponseEntity.internalServerError().body("Feil i kommunikasjon med filadapter")
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

    @PostMapping("/behandling/gjenoppta")
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

    @GetMapping("/pgi/status")
    fun pgiStatus(): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(poppKlient.hentPgiInnlesingStatus().toJson())
        } catch (t: Throwable) {
            log.open.warn("Kunne ikke hente PGI-status")
            log.secure.warn("Kunne ikke hente PGI-status", t)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }

    @PostMapping("/pgi/synkroniser-person")
    fun pgiStatus(
        @RequestParam("fnr") fnr: String,
        @RequestParam("ar") ar: Int,
    ): ResponseEntity<String> {
        return try {
            if (!gyldigFnrInput(fnr)) {
                log.open.warn("synkroniser-person: ugyldige tegn i fnr")
                ResponseEntity.badRequest().body("Ugyldig fnr")
            } else {
                val response = poppKlient.hentPgiForPersonOgÅr(
                    PgiInnlesingHentRequest(fnr, ar)
                ).toJson()
                ResponseEntity.ok(response.toJson())
            }
        } catch (t: Throwable) {
            log.open.warn("Kunne ikke synkronisere person og år")
            log.secure.warn("Kunne ikke synkronisere person og år", t)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }

    @GetMapping("/pgi/list-feilede")
    fun pgiListFeilede(): ResponseEntity<String> {
        return try {
            ResponseEntity.ok(poppKlient.hentFeiledePgi().toJson())
        } catch (t: Throwable) {
            log.open.warn("Kunne ikke liste feilede")
            log.secure.warn("Kunne ikke liste feilede", t)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }


    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        log.open.info("Sa hei")
        return ResponseEntity.ok("pong")
    }

    fun gyldigFnrInput(fnr: String): Boolean {
        return fnr.matches("^[0-9]*$".toRegex())
    }
}