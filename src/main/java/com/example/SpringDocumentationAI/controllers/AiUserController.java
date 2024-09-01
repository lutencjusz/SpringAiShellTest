package com.example.SpringDocumentationAI.controllers;

import com.example.SpringDocumentationAI.model.DtoUser;
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
    public DtoUser registerUser(@RequestBody DtoUser user) {
        return aiUserService.saveUser(user); // Zapisuje nowego użytkownika do bazy
    }

    @PostConstruct
    public void init() {
        DtoUser user = new DtoUser("admin", "admin123#", "ADMIN");
        aiUserService.saveUser(user);
    }
}
