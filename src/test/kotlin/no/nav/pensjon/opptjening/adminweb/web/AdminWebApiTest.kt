package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.Application
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [Application::class])
class AdminWebApiTest{

    @Autowired
    private lateinit var adminWebApi: AdminWebApi
    @Test
    fun `hello world`() {
    }
}