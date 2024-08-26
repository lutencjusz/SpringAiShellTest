package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.model.DtoChatGptRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class SpringAssistantController {

    @Autowired
    private SpringAssistantService springAssistantService;

    @Operation(
            summary = "Przykładowe zapytanie",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DtoChatGptRequest.class),
                            examples = @ExampleObject(
                                    name = "Przykładowe zapytanie",
                                    summary = "Przykładowe body w formacie JSON",
                                    value = "{\"id\":\"1\",\"question\":\"Czy kury są szczęśliwe?\"}"
                            )
                    )
            )
    )
    @Tag(name = "post", description = "Zadaje pytania i otrzymuje odpowiedź")
    @PostMapping
    public ResponseEntity<String> getChatGptAnswer(@org.springframework.web.bind.annotation.RequestBody DtoChatGptRequest question) {
        return ResponseEntity.ok(springAssistantService.getChatGptAnswer(question.getQuestion()));
    }
}
