package com.example.SpringDocumentationAI;

import org.springframework.ai.chat.ChatClient;
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

    @Value("classpath:/prompts/PromptTemplate.st")
    private Resource sbPromptTemplate;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public SpringAssistantService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public String getChatGptAnswer(String question) {
        PromptTemplate promptTemplate = new PromptTemplate(sbPromptTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", question);
        promptParameters.put("documents", String.join("\n", findSimilarDocuments(question)));

        return chatClient.call(promptTemplate.create(promptParameters))
                .getResult()
                .getOutput()
                .getContent();
    }

    private List<String> findSimilarDocuments(String message) {
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(TOP_K));
        return similarDocuments.stream().map(Document::getContent).toList();
    }
}
