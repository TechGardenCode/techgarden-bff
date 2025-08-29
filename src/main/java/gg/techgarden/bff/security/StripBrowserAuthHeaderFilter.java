package gg.techgarden.bff.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class StripBrowserAuthHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getHeader("Authorization") != null) {
            // Reject any browser-supplied bearer token (we use only the session cookie)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Browser Authorization header not allowed");
            return;
        }
        chain.doFilter(request, response);
    }
}