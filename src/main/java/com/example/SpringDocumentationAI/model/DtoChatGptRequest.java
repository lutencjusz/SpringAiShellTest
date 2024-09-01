package com.example.SpringDocumentationAI.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoChatGptRequest {

    private String id;
    @NotNull
    private String question;
}
