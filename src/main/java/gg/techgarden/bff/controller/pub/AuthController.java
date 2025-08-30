package gg.techgarden.bff.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
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
        res.sendRedirect(req.getScheme() + "://" + req.getHeader("host") + "/api/oauth2/authorization/bff");
    }
}