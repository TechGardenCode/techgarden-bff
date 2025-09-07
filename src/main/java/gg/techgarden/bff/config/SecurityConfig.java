package gg.techgarden.bff.config;

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
                        .requestMatchers("/blog/**").authenticated()
                        .requestMatchers("/test/**").authenticated()
                        .anyRequest().authenticated()
                )
//                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogout(registrations))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "SESSION", "TECHGARDEN_SESSION")
                ).csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    LogoutSuccessHandler oidcLogout(ClientRegistrationRepository registrations) {
        OidcClientInitiatedLogoutSuccessHandler handler = new OidcClientInitiatedLogoutSuccessHandler(registrations);
        handler.setPostLogoutRedirectUri("{baseUrl}/");
        return handler;
    }
}
