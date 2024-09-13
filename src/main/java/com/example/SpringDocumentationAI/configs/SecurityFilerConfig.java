package com.example.SpringDocumentationAI.configs;

import com.example.SpringDocumentationAI.CustomAuthenticationFailureHandler;
import com.example.SpringDocumentationAI.services.AiUserService;
import com.example.SpringDocumentationAI.services.CustomOAuth2UserService;
import com.example.SpringDocumentationAI.services.CustomUserDetailsService;
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
    CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private JwtAuthenticationFilterConfig jwtAuthenticationFilterConfig;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .xssProtection(HeadersConfigurer.XXssConfig::disable))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").hasRole("ADMIN")
                        .requestMatchers("/login/**", "/authenticate", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/register/**", "/register-user", "/register-success").permitAll()
                        .requestMatchers("/question", "/upload").authenticated()
                        .requestMatchers("/", "/api/chat/**", "/chat/**", "/load-data").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/download", "/download-faster", "/files-list").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/register-user", "/upload", "/api/chat/**", "/chat/**", "/register", "/authenticate", "/question"))
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll())
                .oauth2Login(oauth2login -> oauth2login
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)) // Custom OAuth2UserService
                        .successHandler((request, response, authentication) -> response.sendRedirect("/")))
                .addFilterBefore(jwtAuthenticationFilterConfig, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
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
        provider.setUserDetailsService(customUserDetailsService);
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
