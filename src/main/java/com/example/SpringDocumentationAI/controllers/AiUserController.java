package com.example.SpringDocumentationAI.controllers;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.model.JwtLoginForm;
import com.example.SpringDocumentationAI.services.AiUserService;
import com.example.SpringDocumentationAI.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiUserController {

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

//    @PostMapping("/register")
//    public DtoUser registerUser(@RequestBody DtoUser user) {
//        return aiUserService.saveUser(user); // Zapisuje nowego użytkownika do bazy
//    }

//    @PostConstruct
    public void init() {
        DtoUser user = new DtoUser("admin", "admin123#", "ADMIN");
        aiUserService.saveUserAndEncodePass(user);
    }

    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody JwtLoginForm loginForm) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(aiUserService.loadUserByUsername(loginForm.getUsername()));
        } else {
            throw new UsernameNotFoundException("Błąd przy autoryzacji");
        }
    }
}
