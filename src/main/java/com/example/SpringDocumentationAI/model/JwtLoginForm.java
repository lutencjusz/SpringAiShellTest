package com.example.SpringDocumentationAI.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JwtLoginForm {
    private String username;
    private String password;
}
