package no.nav.pensjon.opptjening.adminweb.external

import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import no.nav.pensjon.opptjening.adminweb.external.dto.BehandlingStatusResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

class PoppKlientTest {

    private val client: PoppKlient = PoppKlient(
        baseUrl = wiremock.baseUrl(),
        oboTokenInterceptor = MockOnBehalfOfTokenInterceptor()
    )

    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
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

        client.bestillBehandling("""{"first":1,"second":"2"}""").also {
            assertThat(it).isEqualTo(BehandlingStatusResponse.Ok("location"))
        }

        wiremock.allServeEvents.first().also {
            assertThat(it.request.absoluteUrl).isEqualTo("""${wiremock.baseUrl()}/behandling""")
            assertThat(it.request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer obo-token")
            assertThat(it.request.bodyAsString).isEqualTo("""{"first":1,"second":"2"}""")
        }
    }

    @Test
    fun `hentPgiInnlesingStatus parses response correctly`() {
        wiremock.givenThat(
            get(urlPathEqualTo("/pgi/status"))
                .willReturn(
                    ok()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                            """
                            {
                                "sekvensnummer": {
                                    "sekvensnummer": 123,
                                    "aktiv": true
                                }
                            }
                            """.trimIndent()
                        )
                )
        )

        val response = client.hentPgiInnlesingStatus()

        assertThat(response).isEqualTo(
            PgiInnlesingResponse.Status(
                sekvensnummer = PgiInnlesingResponse.Status.PgiSekvensnummerStatus(
                    sekvensnummer = 123L,
                    aktiv = true
                )
            )
        )
    }
}