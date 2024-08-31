package com.example.SpringDocumentationAI.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                        .requestMatchers("/api/chat/**", "/chat/**", "/question/**", "/swagger-ui/**", "/api-docs/**").hasRole("ADMIN")
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/").hasRole("ADMIN")
                        .anyRequest().denyAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/chat/**", "/chat/**") // Wyłącz CSRF dla określonych endpointów
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}
