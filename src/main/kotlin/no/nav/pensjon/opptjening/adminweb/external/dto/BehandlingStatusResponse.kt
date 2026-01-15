package no.nav.pensjon.opptjening.adminweb.external.dto

sealed class BehandlingStatusResponse {
    data class Ok(
        val result: String
    ) : BehandlingStatusResponse()

    data class Error(
        val message: String
    ) : BehandlingStatusResponse()
}
