package gg.techgarden.bff.security;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class TokenExchangeService {

    private final RestClient rest; // plain RestClient
    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;

    public TokenExchangeService(
            RestClient.Builder builder
    ) {
        // Values come from env/config
        this.tokenEndpoint = System.getenv().getOrDefault("KEYCLOAK_TOKEN_ENDPOINT",
                System.getProperty("keycloak.token.endpoint", "https://sso.dev.techgarden.gg/realms/techgarden/protocol/openid-connect/token"));
        this.clientId = System.getenv().getOrDefault("CLIENT_ID", "bff");
        this.clientSecret = System.getenv().getOrDefault("CLIENT_SECRET", "CHANGE_ME");

        // No default auth header; we’ll send basic auth per call.
        this.rest = builder.build();
    }

    public ExchangedToken exchangeUserTokenForAudience(
            String userAccessToken,
            String audience,             // "api.profile" or "api.blog"
            String scopeSpaceSeparated   // e.g. "profile.write" OR "blog.profile.write"
    ) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        form.add("subject_token", userAccessToken);
        form.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");
        form.add("requested_token_type", "urn:ietf:params:oauth:token-type:access_token");
        form.add("audience", audience);
        if (scopeSpaceSeparated != null && !scopeSpaceSeparated.isBlank()) {
            form.add("scope", scopeSpaceSeparated);
        }

        try {
            Map<String, Object> body = rest.post()
                    .uri(tokenEndpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .headers(h -> h.setBasicAuth(clientId, clientSecret))
                    .body(form)
                    .retrieve()
                    .body(Map.class);

            String accessToken = (String) body.get("access_token");
            Integer expiresIn = (Integer) body.get("expires_in");
            String tokenType = (String) body.get("token_type");
            if (accessToken == null) {
                throw new IllegalStateException("Token exchange succeeded but no access_token in response");
            }
            return new ExchangedToken(accessToken, tokenType, expiresIn == null ? 300 : expiresIn);
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException("Token exchange failed: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        }
    }

    public record ExchangedToken(String accessToken, String tokenType, int expiresInSeconds) {}
}
