package com.example.SpringDocumentationAI.configs;

import com.example.SpringDocumentationAI.CustomAuthenticationFailureHandler;
import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.services.AiUserService;
import com.example.SpringDocumentationAI.services.CustomOAuth2UserService;
import com.example.SpringDocumentationAI.services.CustomUserDetailsService;
import com.example.SpringDocumentationAI.services.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityFilerConfig {

    @Autowired
    private MailService mailService;

    @Value("${MAIL_SECRET}")
    private String mailSecret;

    @Value("${HEROKU_APP_NAME}")
    private String appName;

    @Value("${MAIL_ADMIN}")
    private String mailAdmin;

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
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .xssProtection(HeadersConfigurer.XXssConfig::disable))
                .requiresChannel(
                        requiresChannel -> requiresChannel
                                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                                .requiresSecure()
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/swagger-ui/**", "/api-docs/**").hasRole("ADMIN")
                        .requestMatchers("/login/**", "/authenticate", "/css/**", "/js/**", "/images/**","/set-locate/**").permitAll()
                        .requestMatchers("/register/**", "/register-user", "/register-success", "/confirm-registration", "/reset-password/**", "/change-password/**", "/user-verification").permitAll()
                        .requestMatchers("/question", "/upload").authenticated()
                        .requestMatchers("/", "/api/chat/**", "/chat/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/download", "/download-faster", "/files-list", "/delete-file/**", "/load-data", "/progress", "/start-progress").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/files-list", "/load-data", "/delete-file/**", "/change-password/**", "/reset-password/**", "/register-user", "/upload", "/api/chat/**", "/chat/**", "/register", "/authenticate", "/question", "/confirm-registration"))
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(authenticationSuccessHandler())
                        .permitAll())
                .addFilterBefore(jwtAuthenticationFilterConfig, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/login?logout")
                        .logoutSuccessUrl("/login")
                        .permitAll());
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

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            Optional<DtoUser> userOptional = aiUserService.findByUsername(oauthUser.getAttribute("email"));
            if (userOptional.isEmpty()) {
                DtoUser newUser = new DtoUser();
                newUser.setUsername(oauthUser.getAttribute("email"));
                newUser.setEmail(oauthUser.getAttribute("email"));
                newUser.setPassword("password");
                newUser.setRole("USER");
                newUser.setEnabled(false);
                aiUserService.saveUserAndEncodePass(newUser);
            }
            if (!userOptional.get().isEnabled()) {
                DtoUser user = userOptional.get();
                user.setEmail(user.getUsername());
                if (appName == null) {
                    appName = "localhost:8080";
                }
                String linkUserVerification = null;
                try {
                    linkUserVerification = "http://" + appName + "/user-verification?id=" + AiUserService.encrypt(userOptional.get().getUsername(), mailSecret);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    if (mailService.sendEmail(mailAdmin,
                            "Weryfikacja rejestracji w Document AI Analizer",
                            "/templates/user-verification-email.html",
                            linkUserVerification, userOptional.get())) {
                        response.sendRedirect("/login?registerverification"); // Przekierowanie po sukcesie
                    } else {
                        response.sendRedirect("/login?error");
                    }
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                response.sendRedirect("/");
            }
        };
    }
}
