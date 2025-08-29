//package gg.techgarden.bff.config;
//
//import gg.techgarden.bff.security.StripBrowserAuthHeaderFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login/**", "/oauth2/**", "/auth/**", "/actuator/**", "/assets/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/me").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .headers(h -> h
//                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
//                        .frameOptions(frame -> frame.deny())
//                        .xssProtection(Customizer.withDefaults())
//                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).preload(true))
//                )
//                .oauth2Login(Customizer.withDefaults())
//                .logout(lo -> lo.logoutUrl("/auth/logout"));
//
//        // Block any Authorization header sent by the browser
//        http.addFilterBefore(new StripBrowserAuthHeaderFilter(), org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
