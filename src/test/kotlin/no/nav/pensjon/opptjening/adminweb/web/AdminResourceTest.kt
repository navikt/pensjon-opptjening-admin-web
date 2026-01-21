package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.Application
import no.nav.pensjon.opptjening.adminweb.external.FilAdapterKlient
import no.nav.pensjon.opptjening.adminweb.external.PoppKlient
import no.nav.pensjon.opptjening.adminweb.external.dto.FilStatusResponse
import no.nav.pensjon.opptjening.adminweb.external.dto.PgiInnlesingResponse
import no.nav.pensjon.opptjening.adminweb.web.common.TestTokenIssuer
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [Application::class])
@AutoConfigureMockMvc
@EnableMockOAuth2Server
class AdminResourceTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var tokenIssuer: TestTokenIssuer

    @MockitoBean
    private lateinit var filAdapterKlient: FilAdapterKlient

    @MockitoBean
    private lateinit var poppKlient: PoppKlient

    @Test
    fun `rejects request without token`() {
        mvc.perform(MockMvcRequestBuilders.get("/fil/list")).andExpect(status().isUnauthorized)
    }

    @Test
    fun `accepts request with valid token`() {
        whenever(filAdapterKlient.listFiler()).thenReturn(FilStatusResponse.ListOk(emptyList()))

        mvc.perform(MockMvcRequestBuilders.get("/fil/list").header(AUTHORIZATION, tokenIssuer.bearerToken("azure")))
            .andExpect(status().isOk)
    }

    @Test
    fun `returns PGI status as structured JSON`() {
        val statusResponse = PgiInnlesingResponse.Status(
            sekvensnummer = PgiInnlesingResponse.Status.PgiSekvensnummerStatus(
                sekvensnummer = 123L,
                aktiv = true
            )
        )
        whenever(poppKlient.hentPgiInnlesingStatus()).thenReturn(statusResponse)

        mvc.perform(MockMvcRequestBuilders.get("/pgi-innlesing/status").header(AUTHORIZATION, tokenIssuer.bearerToken("azure")))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.sekvensnummer.sekvensnummer").value(123))
            .andExpect(jsonPath("$.sekvensnummer.aktiv").value(true))
    }
}