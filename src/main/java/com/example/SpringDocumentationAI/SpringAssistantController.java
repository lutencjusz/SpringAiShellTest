package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.model.DtoChatGptRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class SpringAssistantController {

    @Autowired
    private SpringAssistantService springAssistantService;

    @GetMapping
    public ResponseEntity<String> getChatGptAnswer(@RequestBody DtoChatGptRequest question) {
        return ResponseEntity.ok(springAssistantService.getChatGptAnswer(question.getQuestion()));
    }
}
