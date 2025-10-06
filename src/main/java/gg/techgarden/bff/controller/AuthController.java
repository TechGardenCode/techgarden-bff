package gg.techgarden.bff.controller;

import gg.techgarden.bff.client.ProfileRestClient;
import gg.techgarden.bff.security.OutboundClients;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final ProfileRestClient profileRestClient;

    @GetMapping("/me")
    public ResponseEntity<OidcUserInfo> me(Authentication auth, HttpServletRequest req, HttpServletResponse res) {
        if (auth == null || !(auth.getPrincipal() instanceof  OidcUser user)) {
            Optional<Cookie> sessionCookie = Arrays.stream(req.getCookies()).filter(cookie -> cookie.getName().equals("SESSION")).findFirst();
            if (sessionCookie.isPresent()) {
                log.warn("No authentication found, but SESSION cookie is present: {}", sessionCookie.get().getValue());
                return new ResponseEntity<>(MultiValueMap.fromSingleValue(Map.of("TG-Try-Silent-Auth", "true")), HttpStatus.UNAUTHORIZED);
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        try {
            profileRestClient.putProfile(user.getSubject());
        } catch (Exception e) {
            log.error("", e);
        }
        return ResponseEntity.ofNullable(((DefaultOidcUser)auth.getPrincipal()).getUserInfo());
    }

    // optional: /api/login -> start OIDC
    @GetMapping("/login")
    public void login(@RequestParam(value = "continue", required = false) String continueUrl, @RequestParam(value = "silent", defaultValue = "0") int silent, HttpServletResponse res) throws IOException {
        if (continueUrl != null) {
            continueUrl = validateContinueUrl(continueUrl);
            res.addHeader("Set-Cookie", "tg_continue=" + continueUrl + "; Max-Age=180; Path=/; HttpOnly; SameSite=Lax");
        }
        String redirectUrl = "/api/oauth2/authorization/bff";
        if (silent == 1) {
            redirectUrl += "?prompt=none";
        }
        res.sendRedirect(redirectUrl);
    }

    String validateContinueUrl(String continueUrl) {
        if (continueUrl == null || continueUrl.isBlank() || !continueUrl.startsWith("/") || continueUrl.startsWith("/api/")) {
            return "/";
        }
        return continueUrl;
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.sendRedirect("/api/logout");
    }
}