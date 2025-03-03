package no.nav.pensjon.opptjening.adminweb.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
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
        azureAdConfig: AzureAdTokenClientConfig,
    ): TokenProvider {
        val config: AzureAdConfig = AzureAdVariableConfig(
            azureAppClientId = azureAdConfig.azureAppClientId,
            azureAppClientSecret = azureAdConfig.azureAppClientSecret,
            targetApiId = appId,
            wellKnownUrl = azureAdConfig.wellKnownUrl,
        )
        return AzureAdTokenProvider(config)
    }
}