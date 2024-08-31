package com.example.SpringDocumentationAI.controllers;

import com.example.SpringDocumentationAI.model.AiUser;
import com.example.SpringDocumentationAI.services.AiUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiUserController {

    @Autowired
    private AiUserService aiUserService;

    @PostMapping("/register")
    public AiUser registerUser(@RequestBody AiUser user) {
        return aiUserService.saveUser(user); // Zapisuje nowego użytkownika do bazy
    }

    @PostConstruct
    public void init() {
        AiUser user = new AiUser("admin", "admin", "ADMIN");
        aiUserService.saveUser(user);
    }
}
