package com.example.SpringDocumentationAI.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoChatGptRequest {

    private String id;
    @NotNull
    @Pattern(regexp = "^(?!\\d+$).*$", message = "Parametr question nie może być liczbą.")
    private String question;
}
