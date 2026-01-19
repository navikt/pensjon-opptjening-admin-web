package no.nav.pensjon.opptjening.adminweb.external

import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import no.nav.pensjon.opptjening.adminweb.Application
import no.nav.pensjon.opptjening.adminweb.web.common.TestTokenIssuer
import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [Application::class])
@AutoConfigureMockMvc
@EnableMockOAuth2Server
class OnBehalfOfTokenIntegrationTest {

    private lateinit var client: PoppKlient

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var tokenIssuer: TestTokenIssuer

    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9991))
            .build()!!
    }

    @Test
    fun `simple request and response`() {
        wiremock.givenThat(
            post(urlPathEqualTo("/behandling"))
                .willReturn(
                    ok()
                        .withHeader(HttpHeaders.LOCATION, "location")
                )
        )

        val initialToken = tokenIssuer.bearerToken(
            issuerId = "azure",
            audience = "pensjon-opptjening-admin-web",
            subject = "person"
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/behandling/bestill")
                .param("request", "whatever")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, initialToken)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("""{"result":"location"}"""))

        wiremock.allServeEvents.first().also { event ->
            assertThat(event.request.absoluteUrl).isEqualTo("""${wiremock.baseUrl()}/behandling""")
            assertThat(event.request.getHeader(HttpHeaders.AUTHORIZATION)).isNotNull().also {
                JwtToken(event.request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "")).also {
                    assertThat(it.encodedToken).isNotEqualTo(initialToken.replace("Bearer ", ""))
                    assertThat(it.jwtClaimsSet.audience.single()).isEqualTo("api://cluster.namespace.other-api-app-name/.default")
                    assertThat(it.subject).isEqualTo("person")
                }
            }
            assertThat(event.request.bodyAsString).isEqualTo("""whatever""")
        }
    }
}