package com.example.SpringDocumentationAI.configs;

import com.example.SpringDocumentationAI.services.AiUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers// Prevents the page from being displayed in a frame or iframe
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))  // Enforces HTTPS// Restricts resources to the same origin
                        .xssProtection(HeadersConfigurer.XXssConfig::disable) // Enables XSS protection
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/question/**", "/swagger-ui/**", "/api-docs/**").hasRole("ADMIN")
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login/**", "/register/**").permitAll()
                        .requestMatchers("/", "/api/chat/**", "/chat/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().denyAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/chat/**", "/chat/**", "/register/**", "/question/**") // Wyłącz CSRF dla określonych endpointów
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    /**
     * Metoda do konfiguracji provider'a autoryzacji, który będzie używany do autoryzacji użytkowników.
     * Musi być w klasie SecurityConfig, ponieważ string boot nie rozpozjane haseł, jeśli nie jest zadeklarowana w tej klasie.
     *
     * @return - obiekt provider'a autoryzacji
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Metoda do zwracania obiektu zarządzającego autoryzacją użytkowników.
     * Musi być w klasie SecurityConfig, ponieważ string boot nie rozpozjane haseł, jeśli nie jest zadeklarowana w tej klasie.
     *
     * @return - obiekt zarządzający autoryzacją użytkowników
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }
}
