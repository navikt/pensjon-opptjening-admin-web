package no.nav.pensjon.opptjening.adminweb.web.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pensjon.opptjening.azure.ad.client.TokenProvider
import pensjon.opptjening.azure.ad.client.mock.MockTokenProvider

@Configuration
class TokenProviderConfig {

    @Bean
    fun filadapterTokenProvider(): TokenProvider = MockTokenProvider(MOCK_TOKEN)

    @Bean
    fun poppTokenProvider(): TokenProvider = MockTokenProvider(MOCK_TOKEN)

    companion object {
        const val MOCK_TOKEN = "test.token.test"
    }
}