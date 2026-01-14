package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.external.dto.BehandlingStatusResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.FilStatusResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingHentRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingSettSekvensnummerRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingSettSekvensnummerStatusRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingSettSekvensnummerTilForsteRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingSlettSekvensnummerRequest
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiStatusResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.SekvensnummerStatus
import no.nav.pensjon.opptjening.adminweb.utils.AuditLogUtils
import no.nav.pensjon.opptjening.adminweb.utils.ValidationUtils
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.LoggerFactory
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
    tokenValidationContextHolder: TokenValidationContextHolder,
) {
    private val auditLogUtils = AuditLogUtils(tokenValidationContextHolder)

    companion object {
        private val secure = LoggerFactory.getLogger("secure")
    }

    @GetMapping("/fil/list", produces = [MediaType.TEXT_PLAIN_VALUE])
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

    @PostMapping("/fil/overfor")
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

    @PostMapping("/behandling")
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

    @PostMapping("/behandling/gjenoppta")
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

    @GetMapping("/pgi/status")
    fun pgiStatus(): ResponseEntity<PgiStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.READ,
            function = "pgi status",
        )
        return try {
            ResponseEntity.ok(poppKlient.hentPgiInnlesingStatus())
        } catch (t: Throwable) {
            secure.warn("Kunne ikke hente PGI-status", t)
            ResponseEntity.internalServerError().body(PgiStatusResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/pgi/sett-sekvensnummer")
    fun pgiSettSekvensnummer(
        @RequestParam("dato") datoInput: String,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            function = "pgi sett sekvensnummer",
            begrunnelse = begrunnelse,
            informasjon = datoInput,
        )
        val dato = if (datoInput == "") null else datoInput
        return try {
            if (dato != null && !ValidationUtils.gyldigIsoDato(dato)) {
                secure.warn("ugyldig format på dato")
                ResponseEntity.badRequest().body(PgiStatusResponse.Error("Ugyldig dato"))
            } else if (dato == null) {
                secure.warn("Forsøk på å sette tom dato")
                ResponseEntity.badRequest().body(PgiStatusResponse.Error("TODO: Tom dato er foreløpig ikke støttet"))
            } else {
                ResponseEntity.ok(
                    poppKlient.settSekvensnummer(
                        PgiInnlesingSettSekvensnummerRequest(
                            dato = ValidationUtils.parseIsoDato(dato),
                        )
                    )
                )
            }
        } catch (t: Throwable) {
            secure.warn("Kunne ikke sette sekvensnummer", t)
            ResponseEntity.internalServerError().body(PgiStatusResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/pgi/sett-sekvensnummer-til-forste")
    fun pgiSettSekvensnummerTilForste(
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            function = "pgi sett sekvensnummer forste",
            begrunnelse = begrunnelse,
        )
        return try {
            val response = poppKlient.settSekvensnummer(
                PgiInnlesingSettSekvensnummerTilForsteRequest(
                    resetSekvensnummer = false
                )
            )
            ResponseEntity.ok(response)
        } catch (t: Throwable) {
            secure.warn("Kunne ikke sette sekvensnummer", t)
            ResponseEntity.internalServerError().body(PgiStatusResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/pgi/slett-sekvensnummer")
    fun pgiSlettSekvensnummer(
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            function = "pgi sett sekvensnummer forste",
            begrunnelse = begrunnelse,
        )
        return try {
            val response = poppKlient.slettSekvensnummer(
                PgiInnlesingSlettSekvensnummerRequest(
                    slettSekvensnummer = true
                )
            )
            ResponseEntity.ok(response)
        } catch (t: Throwable) {
            secure.warn("Kunne ikke slette sekvensnummer", t)
            ResponseEntity.internalServerError().body(PgiStatusResponse.Error("Intern feil"))
        }
    }


    @PostMapping("/pgi/synkroniser-person")
    fun pgiSynkroniserPerson(
        @RequestParam("fnr") fnr: String,
        @RequestParam("ar") ar: Int,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            function = "pgi synkroniser person",
            informasjon = ar.toString(),
            begrunnelse = begrunnelse,
            fnr = fnr,
        )
        return try {
            if (!ValidationUtils.gyldigFnrInput(fnr)) {
                secure.warn("synkroniser-person: ugyldige tegn i fnr")
                ResponseEntity.badRequest().body(PgiStatusResponse.Error("Ugyldig fnr"))
            } else {
                val response = poppKlient.hentPgiForPersonOgÅr(
                    PgiInnlesingHentRequest(fnr, ar)
                )
                ResponseEntity.ok(response)
            }
        } catch (t: Throwable) {
            secure.warn("Kunne ikke synkronisere person og år", t)
            ResponseEntity.internalServerError().body(PgiStatusResponse.Error("Intern feil"))
        }
    }

    @GetMapping("/pgi/list-feilede")
    fun pgiListFeilede(): ResponseEntity<PgiStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.READ,
            function = "pgi list feilede",
        )
        return try {
            ResponseEntity.ok(poppKlient.hentFeiledePgi())
        } catch (t: Throwable) {
            secure.warn("Kunne ikke liste feilede", t)
            ResponseEntity.internalServerError().body(PgiStatusResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/pgi/sett-sekvensnummer-status")
    fun pgiSettStatus(
        @RequestParam("tilstand") tilstand: String,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiStatusResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.WRITE,
            function = "pgi sett status",
            informasjon = "tilstand=$tilstand",
            begrunnelse = begrunnelse,
        )

        return try {
            val status =
                poppKlient.settPgiSekvensnummerStatus(
                    PgiInnlesingSettSekvensnummerStatusRequest(
                        status = when (tilstand) {
                            "aktiv" -> SekvensnummerStatus.AKTIV
                            "pauset" -> SekvensnummerStatus.PAUSET
                            else -> throw RuntimeException("Ugyldig tilstand: $tilstand")
                        }
                    )
                )
            ResponseEntity.ok(status)
        } catch (t: Throwable) {
            secure.warn("PGI: Kunne ikke sette status", t)
            ResponseEntity.internalServerError().body(PgiStatusResponse.Error("Intern feil"))
        }
    }


    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        secure.info("Sa hei")
        return ResponseEntity.ok("pong")
    }
}