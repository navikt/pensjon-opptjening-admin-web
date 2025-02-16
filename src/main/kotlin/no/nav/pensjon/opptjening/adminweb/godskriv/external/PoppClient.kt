package no.nav.pensjon.opptjening.adminweb.godskriv.external

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pensjon.opptjening.azure.ad.client.TokenProvider

internal class PoppClient(
    private val baseUrl: String,
    private val tokenProvider: TokenProvider,
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

}

class PoppClientExecption(message: String, throwable: Throwable) : RuntimeException(message, throwable)