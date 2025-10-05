package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.log.NAVLog
import no.nav.pensjon.opptjening.adminweb.utils.JsonUtils.toJson
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingHentRequest
import no.nav.popp.web.api.endpoint.pgi.model.PgiInnlesingSettSekvensnummerRequest
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@Protected
class AdminResource(
    private val filAdapterKlient: FilAdapterKlient,
    private val poppKlient: PoppKlient,
) {
    companion object {
        private val log = NAVLog(AdminResource::class)
    }

    @GetMapping("/fil/list", produces = [MediaType.TEXT_PLAIN_VALUE])
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

    @PostMapping("/fil/overfor")
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

    @PostMapping("/pgi/sett-sekvensnumer")
    fun pgiStatus(
        @RequestParam dato: String,
    ): ResponseEntity<String> {
        val dato = if (dato == "") null else dato
        return try {
            if (dato != null && !gyldigIsoDato(dato)) {
                log.open.warn("ugyldig format på dato")
                ResponseEntity.badRequest().body("Ugyldig dato")
            } else if (dato == null) {
                ResponseEntity.badRequest().body("TODO: Tom dato er foreløpig ikke støttet")
            } else {
                val response = poppKlient.settSekvensnummer(
                    PgiInnlesingSettSekvensnummerRequest(
                        dato = parseIsoDato(dato),
                    )
                ).toJson()
                ResponseEntity.ok(response.toJson())
            }
        } catch (t: Throwable) {
            log.open.warn("Kunne ikke sette sekvensnummer")
            log.secure.warn("Kunne ikke sette sekvensnummer", t)
            ResponseEntity.internalServerError().body("Intern feil")
        }
    }

    private fun gyldigIsoDato(dato: String): Boolean {
        return dato.matches("^\\d{4}-\\d{2}-\\d{2}$".toRegex())
    }

    private fun parseIsoDato(dato: String?): LocalDate {
        return LocalDate.parse(dato, DateTimeFormatter.ISO_LOCAL_DATE)
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