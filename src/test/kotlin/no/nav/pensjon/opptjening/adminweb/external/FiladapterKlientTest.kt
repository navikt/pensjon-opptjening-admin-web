package no.nav.pensjon.opptjening.adminweb.external

import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

class FiladapterKlientTest {

    private val client: FilAdapterKlient = FilAdapterKlient(
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
        fun id() = UUID.randomUUID().toString()

        wiremock.givenThat(
            get(urlPathEqualTo("/list"))
                .willReturn(
                    ok()
                        .withBody("""{"filer":[{"filnavn":"fil1","size":123,"lagretMedId":"${id()}", "lagresMedId":[]}]}""")
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        )

        client.listFiler().also {
            assertThat(
                it.filer.map { it.filnavn }.contains("fil1")
            ).isTrue()
        }
    }
}