package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.utils.AuditLogUtils
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}