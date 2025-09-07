package gg.techgarden.bff.controller;

import gg.techgarden.bff.security.OutboundClients;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Value("${bff.services.profile:http://localhost:8082}")
    private String profileServiceUrl;
//    private final RestClient restClient;

    private final OutboundClients outboundClients;

    @GetMapping("/me")
    public OidcUserInfo me(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof  OidcUser user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        try {
            outboundClients.profileClient(auth)
                    .put()
                    .uri(profileServiceUrl + "/profiles/{sub}", user.getSubject())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return ((DefaultOidcUser)auth.getPrincipal()).getUserInfo();
    }

    // optional: /api/login -> start OIDC
    @GetMapping("/login")
    public void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.sendRedirect("/api/oauth2/authorization/bff");
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.sendRedirect("/api/logout");
    }
}