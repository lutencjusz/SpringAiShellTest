package com.example.SpringDocumentationAI.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpringAssistantService {

    private static final int TOP_K = 3;
    private static final int MINIMUM_QUESTION_LENGHT = 10;

    @Value("classpath:/prompts/PromptTemplate.st")
    private Resource sbPromptTemplate;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public SpringAssistantService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String getChatGptAnswer(String question) {
        if (question == null) {
            throw new IllegalArgumentException("Field question is necessary");
        }
        if (question.isBlank()) {
            throw new IllegalArgumentException("Field question can't be empty.");
        }
        if (isNumeric(question)) {
            throw new IllegalArgumentException("Field question can't be a number.");
        }
        if (question.length() < MINIMUM_QUESTION_LENGHT) {
            throw new IllegalArgumentException(String.format("Field question must have minimum %s letters", MINIMUM_QUESTION_LENGHT));
        }
        PromptTemplate promptTemplate = new PromptTemplate(sbPromptTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", question);
        promptParameters.put("documents", String.join("\n", findSimilarDocuments(question)));

        return chatClient.prompt(promptTemplate.create(promptParameters))
                .call()
                .content();
    }

    private List<String> findSimilarDocuments(String message) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(TOP_K));
        return similarDocuments.stream().map(Document::getContent).toList();
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String translate(String text, String targetLanguage) {
        return getChatGptAnswer("Przetłumacz tylko frazy w języku polskim '"
                + text + "' na język określony kodem '"
                + targetLanguage + "'. Zwróć tylko frazy bez żadnych ozdobników. Każda linia powinna kończyć się znakiem nowej linii.");
    }
}
