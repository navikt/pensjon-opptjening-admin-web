package no.nav.pensjon.opptjening.adminweb.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import pensjon.opptjening.azure.ad.client.AzureAdConfig
import pensjon.opptjening.azure.ad.client.AzureAdTokenProvider
import pensjon.opptjening.azure.ad.client.AzureAdVariableConfig
import pensjon.opptjening.azure.ad.client.TokenProvider

@Configuration
@Profile("dev-gcp", "prod-gcp")
class TokenProviderConfig {

    @Bean
    fun tokenProvider(
        @Value("\${FILADAPTER_API_ID}") appId: String,
        @Value("\${AZURE_APP_CLIENT_ID}") azureAppClientId: String,
        @Value("\${AZURE_APP_CLIENT_SECRET}") azureAppClientSecret: String,
        @Value("\${AZURE_APP_WELL_KNOWN_URL}") wellKnownUrl: String,
    ): TokenProvider {
        val config: AzureAdConfig = AzureAdVariableConfig(
            azureAppClientId = azureAppClientId,
            azureAppClientSecret = azureAppClientSecret,
            targetApiId = appId,
            wellKnownUrl = wellKnownUrl,
        )
        return AzureAdTokenProvider(config)
    }
}