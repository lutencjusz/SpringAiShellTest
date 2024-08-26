package com.example.SpringDocumentationAI.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoChatGptRequest {

    @Nullable
    private String id;
    @NotNull
    private String question;
}
