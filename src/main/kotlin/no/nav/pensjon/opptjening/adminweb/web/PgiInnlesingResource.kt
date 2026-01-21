package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.external.dto.*
import no.nav.pensjon.opptjening.adminweb.utils.AuditLogUtils
import no.nav.pensjon.opptjening.adminweb.utils.ValidationUtils
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pgi-innlesing")
@Protected
class PgiInnlesingResource(
    private val poppKlient: PoppKlient,
    tokenValidationContextHolder: TokenValidationContextHolder,
) {
    private val auditLogUtils = AuditLogUtils(tokenValidationContextHolder)

    companion object {
        private val secure = LoggerFactory.getLogger("secure")
    }

    @GetMapping("/status")
    fun pgiStatus(): ResponseEntity<PgiInnlesingResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.READ,
            function = "pgi status",
        )
        return try {
            ResponseEntity.ok(poppKlient.hentPgiInnlesingStatus())
        } catch (t: Throwable) {
            secure.warn("Kunne ikke hente PGI-status", t)
            ResponseEntity.internalServerError().body(PgiInnlesingResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/sett-sekvensnummer")
    fun pgiSettSekvensnummer(
        @RequestParam("dato") datoInput: String,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiInnlesingResponse> {
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
                ResponseEntity.badRequest().body(PgiInnlesingResponse.Error("Ugyldig dato"))
            } else if (dato == null) {
                secure.warn("Forsøk på å sette tom dato")
                ResponseEntity.badRequest().body(PgiInnlesingResponse.Error("TODO: Tom dato er foreløpig ikke støttet"))
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
            ResponseEntity.internalServerError().body(PgiInnlesingResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/sett-sekvensnummer-til-forste")
    fun pgiSettSekvensnummerTilForste(
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiInnlesingResponse> {
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
            ResponseEntity.internalServerError().body(PgiInnlesingResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/slett-sekvensnummer")
    fun pgiSlettSekvensnummer(
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiInnlesingResponse> {
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
            ResponseEntity.internalServerError().body(PgiInnlesingResponse.Error("Intern feil"))
        }
    }


    @PostMapping("/synkroniser-person")
    fun pgiSynkroniserPerson(
        @RequestParam("fnr") fnr: String,
        @RequestParam("ar") ar: Int,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiInnlesingResponse> {
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
                ResponseEntity.badRequest().body(PgiInnlesingResponse.Error("Ugyldig fnr"))
            } else {
                val response = poppKlient.hentPgiForPersonOgÅr(
                    PgiInnlesingHentRequest(fnr, ar)
                )
                ResponseEntity.ok(response)
            }
        } catch (t: Throwable) {
            secure.warn("Kunne ikke synkronisere person og år", t)
            ResponseEntity.internalServerError().body(PgiInnlesingResponse.Error("Intern feil"))
        }
    }

    @GetMapping("/list-feilede")
    fun pgiListFeilede(): ResponseEntity<PgiInnlesingResponse> {
        auditLogUtils.auditLog(
            operation = AuditLogUtils.Operation.READ,
            function = "pgi list feilede",
        )
        return try {
            ResponseEntity.ok(poppKlient.hentFeiledePgi())
        } catch (t: Throwable) {
            secure.warn("Kunne ikke liste feilede", t)
            ResponseEntity.internalServerError().body(PgiInnlesingResponse.Error("Intern feil"))
        }
    }

    @PostMapping("/sett-sekvensnummer-status")
    fun pgiSettStatus(
        @RequestParam("tilstand") tilstand: String,
        @RequestParam("begrunnelse") begrunnelse: String,
    ): ResponseEntity<PgiInnlesingResponse> {
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
            ResponseEntity.internalServerError().body(PgiInnlesingResponse.Error("Intern feil"))
        }
    }
}