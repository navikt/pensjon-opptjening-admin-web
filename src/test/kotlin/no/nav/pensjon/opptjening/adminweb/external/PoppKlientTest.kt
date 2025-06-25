package no.nav.pensjon.opptjening.adminweb.external

import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

class PoppKlientTest {

    private val client: PoppKlient = PoppKlient(
        baseUrl = wiremock.baseUrl(),
        nextToken = { "token" }
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
                        .withBody("""ok""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        )

        client.bestillBehandling("""{"first":1,"second":"2"}""").also {
            assertThat(it).isEqualTo("ok")
        }

        wiremock.allServeEvents.first().also {
            assertThat(it.request.absoluteUrl).isEqualTo("""${wiremock.baseUrl()}/behandling""")
            assertThat(it.request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer token")
            assertThat(it.request.bodyAsString).isEqualTo("""{"first":1,"second":"2"}""")
        }
    }
}