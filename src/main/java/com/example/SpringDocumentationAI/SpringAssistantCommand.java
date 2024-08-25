package com.example.SpringDocumentationAI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.shell.command.annotation.Command;

@Command
public class SpringAssistantCommand {

    @Autowired
    private SpringAssistantService springAssistantService;

    @Command(command = "q")
    public String question(@DefaultValue(value = "What is Spring Boot") String message) {
        return springAssistantService.getChatGptAnswer(message);
    }
}
