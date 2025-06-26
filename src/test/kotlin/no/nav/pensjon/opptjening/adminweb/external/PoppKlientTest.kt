package no.nav.pensjon.opptjening.adminweb.external

import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.http.HttpHeaders

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
            assertThat(it).isEqualTo("location")
        }

        wiremock.allServeEvents.first().also {
            assertThat(it.request.absoluteUrl).isEqualTo("""${wiremock.baseUrl()}/behandling""")
            assertThat(it.request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer obo-token")
            assertThat(it.request.bodyAsString).isEqualTo("""{"first":1,"second":"2"}""")
        }
    }
}