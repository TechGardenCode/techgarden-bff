package gg.techgarden.bff.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        return (auth == null)
                ? Map.of("authenticated", false)
                : Map.of("authenticated", true, "name", auth.getName());
    }

    // optional: /api/login -> start OIDC
    @GetMapping("/login")
    public void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        log.info(req.getHeader("Host"));
        log.info(req.getHeader("X-Forwarded-Host"));
        log.info(req.getHeader("X-Forwarded-Proto"));
//        res.sendRedirect(req.getScheme() + "://" + req.getHeader("host") + "/api/oauth2/authorization/bff");
        res.sendRedirect("/api/oauth2/authorization/bff");
    }
}