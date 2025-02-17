package no.nav.pensjon.opptjening.adminweb.external

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class PoppClient(
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

}

class PoppClientExecption(message: String, throwable: Throwable) : RuntimeException(message, throwable)