package no.nav.pensjon.opptjening.adminweb.web

import no.nav.security.token.support.core.api.Protected
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Protected
class AdminWebApi() {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(AdminWebApi::class.java)
        private val secureLog: Logger = LoggerFactory.getLogger("secure")
    }

    @GetMapping("/hello")
    fun sayHello(
    ): ResponseEntity<String> {
        return ResponseEntity.ok("Hello world")
    }

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        log.info("Sa hei")
        return ResponseEntity.ok("pong")
    }

}