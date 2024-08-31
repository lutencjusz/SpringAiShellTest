package com.example.SpringDocumentationAI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Entity
@Builder
public class AiUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    @Getter
    private String role;

    public AiUser(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Implementacja metod z interfejsu UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Możesz dostosować logikę w zależności od wymagań
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Możesz dostosować logikę w zależności od wymagań
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Możesz dostosować logikę w zależności od wymagań
    }

    @Override
    public boolean isEnabled() {
        return true; // Możesz dostosować logikę w zależności od wymagań
    }
}
