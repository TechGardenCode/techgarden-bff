package gg.techgarden.bff.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OutboundClients {

    private final OAuth2AuthorizedClientManager clientManager;
    private final OAuth2AuthorizedClientService clientService;
    private final TokenExchangeService tokenExchangeService;
    private final RestClient.Builder restBuilder;

    public OutboundClients(
            OAuth2AuthorizedClientManager clientManager,
            OAuth2AuthorizedClientService clientService,
            TokenExchangeService tokenExchangeService,
            RestClient.Builder restBuilder) {
        this.clientManager = clientManager;
        this.clientService = clientService;
        this.tokenExchangeService = tokenExchangeService;
        this.restBuilder = restBuilder;
    }

    /** Fetch current user's access token (the BFF client) from the session store. */
    private String currentUserAccessToken(Authentication auth) {
        var client = clientService.loadAuthorizedClient("bff", auth.getName());
        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("No authorized client for current user");
        }
        return client.getAccessToken().getTokenValue();
    }

    /** Client-credentials for the BFF (only if you configured a client-credentials registration). */
    private String clientCredentialsToken() {
        var req = OAuth2AuthorizeRequest.withClientRegistrationId("bff") // or another reg id if you have one for m2m
                .principal("bff")
                .build();
        var client = clientManager.authorize(req);
        OAuth2AccessToken at = client.getAccessToken();
        return at.getTokenValue();
    }

    /** RestClient that always attaches a given Bearer token. */
    private RestClient authed(String bearerToken) {
        return restBuilder
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(bearerToken);
                    return execution.execute(request, body);
                })
                .build();
    }

    /** Example: call Profile with exchanged user token. */
    public RestClient profileClient(Authentication auth) {
        var exchanged = tokenExchangeService.exchangeUserTokenForAudience(
                currentUserAccessToken(auth),
                ApiTargets.PROFILE_AUD,
                null
        );
        return authed(exchanged.accessToken());
    }

    /** Example: optional—BFF calling blog m2m (not used in your primary path). */
    public RestClient blogM2MClient() {
        return authed(clientCredentialsToken());
    }
}
