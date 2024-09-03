package com.example.SpringDocumentationAI.services;

import com.example.SpringDocumentationAI.model.CustomOAuth2User;
import com.example.SpringDocumentationAI.model.DtoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private AiUserService aiUserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        User user;

        // Check if user already exists in the database
        Optional<DtoUser> userOptional = aiUserService.findByUsername(email);
        if (userOptional.isPresent()) {
            return oauth2User;
        } else {
            // Create a new user and save to the database
            user = new User(oauth2User.getAttribute("name"), email, Objects.requireNonNull(oauth2User.getAttribute("picture")));
            DtoUser newUser = new DtoUser();
            newUser.setUsername(email);
            newUser.setPassword("password");
            newUser.setRole("USER");
            aiUserService.saveUser(newUser);
        }
        return new CustomOAuth2User(user, oauth2User.getAttributes());
    }
}


