package gg.techgarden.bff.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @GetMapping("/me")
    public OidcUserInfo me(Authentication auth) {
        if (auth == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        return ((DefaultOidcUser)auth.getPrincipal()).getUserInfo();
    }

    // optional: /api/login -> start OIDC
    @GetMapping("/login")
    public void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.sendRedirect("/api/oauth2/authorization/bff");
    }
}