package gg.techgarden.bff.config;

import gg.techgarden.bff.security.SpaCsrfTokenRequestHandler;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository registrations) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/blog/posts/metadata").permitAll()
                        .requestMatchers(HttpMethod.GET, "/blog/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/profile/profiles/**").permitAll()
                        .requestMatchers("/blog/**").authenticated()
                        .requestMatchers("/test/**").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(login -> login
                        .successHandler((req, res, auth) -> {
                            String continueUrl = "/";
                            if (req.getCookies() != null) {
                                var continueCookie =  java.util.Arrays.stream(req.getCookies())
                                        .filter(cookie -> cookie.getName().equals("tg_continue"))
                                        .findFirst();
                                if (continueCookie.isPresent()) {
                                    continueUrl = continueCookie.get().getValue();
                                    Cookie clearCookie = new Cookie("tg_continue", null);
                                    clearCookie.setPath("/");
                                    clearCookie.setMaxAge(0);
                                    res.addCookie(clearCookie);
                                }
                            }
                            res.sendRedirect(continueUrl);
                        })
                )
                .oauth2Client(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/logout"))
                        .logoutSuccessHandler(oidcLogout(registrations))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "SESSION", "TECHGARDEN_SESSION")
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                );
        return http.build();
    }

    @Bean
    LogoutSuccessHandler oidcLogout(ClientRegistrationRepository registrations) {
        OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(registrations);
        handler.setPostLogoutRedirectUri("{baseUrl}/");
        return handler;
    }
}
