package com.example.SpringDocumentationAI.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Dodaj rolę "ADMIN" dla zalogowanego użytkownika
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");

        return new DefaultOAuth2User(
                Collections.singleton(authority),
                oAuth2User.getAttributes(),
                "username");  // Nazwa atrybutu, który identyfikuje użytkownika, np. "name" lub "email"
    }
}

