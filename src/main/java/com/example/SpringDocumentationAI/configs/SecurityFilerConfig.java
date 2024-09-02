package com.example.SpringDocumentationAI.configs;

import com.example.SpringDocumentationAI.services.AiUserService;
import com.example.SpringDocumentationAI.services.CustomOAuth2UserService;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityFilerConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationFilterConfig jwtAuthenticationFilterConfig;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").hasRole("ADMIN")
                        .requestMatchers("/login/**", "/register/**", "/authenticate", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/question").authenticated()
                        .requestMatchers("/", "/api/chat/**", "/chat/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/chat/**", "/chat/**", "/register", "authenticate", "/question")
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .oauth2Login(oauth2login -> oauth2login
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> response.sendRedirect("/"))
                        .failureHandler((request, response, exception) -> response.sendRedirect("/login"))
                )
                .addFilterBefore(jwtAuthenticationFilterConfig, UsernamePasswordAuthenticationFilter.class);
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
