package no.nav.pensjon.opptjening.adminweb.web.common

import com.nimbusds.jose.JOSEObjectType
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.springframework.stereotype.Service

@Service
class TestTokenIssuer(
    private val oauth2Server: MockOAuth2Server
) {

    fun token(
        issuerId: String,
        audience: String = "pensjon-opptjening-admin-web",
        subject: String = "subject"
    ): String {
        return oauth2Server.issueToken(
            issuerId = issuerId,
            clientId = "theclientid",
            tokenCallback = DefaultOAuth2TokenCallback(
                issuerId = issuerId,
                subject = subject,
                typeHeader = JOSEObjectType.JWT.type,
                audience = listOf(audience),
                claims = mapOf("" to ""),
                expiry = 3600
            )
        ).serialize()
    }

    fun bearerToken(
        issuerId: String,
        audience: String = "pensjon-opptjening-admin-web",
        subject: String = "subject"
    ): String {
        return """Bearer ${token(issuerId, audience, subject)}"""
    }
}