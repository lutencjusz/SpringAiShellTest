package com.example.SpringDocumentationAI.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOAuth2User implements OAuth2User {

    @Getter
    private final DtoUser dtoUser;
    private Map<String, Object> attributes;

    public CustomOAuth2User(DtoUser dtouser, Map<String, Object> attributes) {
        this.dtoUser = dtouser;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return dtoUser.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return dtoUser.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    public String getEmail() {
        return dtoUser.getEmail(); // lub inny klucz dla adresu email
    }

    public boolean isEnabled() {
        return dtoUser.isEnabled();
    }
}
