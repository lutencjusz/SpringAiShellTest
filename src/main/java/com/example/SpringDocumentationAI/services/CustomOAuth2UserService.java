package com.example.SpringDocumentationAI.services;

import com.example.SpringDocumentationAI.model.CustomOAuth2User;
import com.example.SpringDocumentationAI.model.DtoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private AiUserService aiUserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        DtoUser newUser;
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        Map<String, Object> attributes = oauth2User.getAttributes();
        OAuth2User actualOAuth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );

        // Check if user already exists in the database
        Optional<DtoUser> userOptional = aiUserService.findByUsername(email);
        if (userOptional.isPresent()) {
            return actualOAuth2User;
        } else {
            // Create a new user and save to the database
            newUser = new DtoUser();
            newUser.setUsername(email);
            newUser.setEmail(email);
            newUser.setPassword("password");
            newUser.setRole("USER");
            newUser.setEnabled(false);
            aiUserService.saveUserAndEncodePass(newUser);
        }
        return new CustomOAuth2User(newUser, oauth2User.getAttributes());
    }
}


