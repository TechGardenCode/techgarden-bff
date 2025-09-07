package gg.techgarden.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

@Configuration
public class OAuthClientConfig {

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository repo) {
        return new InMemoryOAuth2AuthorizedClientService(repo);
    }

    /** For user flows (resolves current user's authorized client) and for client-credentials. */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository repo,
            OAuth2AuthorizedClientService svc) {

        var provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .clientCredentials()
                .build();

        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(repo, svc);
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }
}