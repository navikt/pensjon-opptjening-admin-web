package no.nav.pensjon.opptjening.adminweb.external

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class MockOnBehalfOfTokenInterceptor(val token: String = "obo-token") : ClientHttpRequestInterceptor {
    override fun intercept(
        req: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        req.headers.setBearerAuth(token)
        return execution.execute(req, body)
    }
}