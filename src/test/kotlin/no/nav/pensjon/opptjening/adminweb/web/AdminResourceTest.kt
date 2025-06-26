package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.Application
import no.nav.pensjon.opptjening.adminweb.web.common.TestTokenIssuer
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
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
    private lateinit var adminResource: AdminResource

    @Test
    fun `rejects request without token`() {
        mvc.perform(MockMvcRequestBuilders.get("/list")).andExpect(status().isUnauthorized)
    }

    @Test
    fun `accepts request with valid token`() {
        whenever(adminResource.listFiler()).thenReturn(ResponseEntity.ok().body("success"))

        mvc.perform(MockMvcRequestBuilders.get("/list").header(AUTHORIZATION, tokenIssuer.bearerToken("azure")))
            .andExpect(status().isOk)
    }
}