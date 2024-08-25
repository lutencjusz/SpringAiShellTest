package com.example.SpringDocumentationAI.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoChatGptRequest {
    private String id;
    private String question;
}
