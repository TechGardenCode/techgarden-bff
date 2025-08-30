package gg.techgarden.bff.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Component
public class TokenService {
    private final OAuth2AuthorizedClientManager manager;

    public TokenService(OAuth2AuthorizedClientManager manager) {
        this.manager = manager;
    }

    public String getAccessToken(Authentication authentication, HttpServletRequest request) {
        if (!(authentication instanceof OAuth2AuthenticationToken oat)) return null;

        var authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(oat.getAuthorizedClientRegistrationId())
                .principal(authentication)
                .attribute(HttpServletRequest.class.getName(), request)
                .build();

        OAuth2AuthorizedClient client = manager.authorize(authorizeRequest);
        if (client == null) return null;

        OAuth2AccessToken token = client.getAccessToken();
        return token != null ? token.getTokenValue() : null;
    }
}