package com.example.SpringDocumentationAI.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class EncoderConfig {

    /**
     * Metoda do zwracania obiektu szyfrującego hasła. Musi być w wydzielonej klasie, ponieważ string boot nie chce się uruchomić,
     * jeśli znajduje się w klasie SecurityConfig
     *
     * @return - obiekt szyfrujący hasła
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
