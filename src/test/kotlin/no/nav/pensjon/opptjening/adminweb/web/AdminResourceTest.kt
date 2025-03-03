package no.nav.pensjon.opptjening.adminweb.web

import no.nav.pensjon.opptjening.adminweb.Application
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [Application::class])
class AdminResourceTest{

    @Autowired
    private lateinit var adminResource: AdminResource

    @Test
    fun `hello world`() {
    }
}