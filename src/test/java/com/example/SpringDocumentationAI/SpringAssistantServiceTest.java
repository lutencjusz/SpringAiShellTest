package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.services.SpringAssistantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("Test")
class SpringAssistantServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilderMock;

    @Mock
    private VectorStore vectorStoreMock;

    @Mock
    private ChatClient chatClientMock;

    @Mock
    private Resource sbPromptTemplate;

    private SpringAssistantService springAssistantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(chatClientBuilderMock.build()).thenReturn(chatClientMock);
        springAssistantService = new SpringAssistantService(chatClientBuilderMock, vectorStoreMock);
    }

    @Test
    @DisplayName("Pojawił się wyjątek, gdy pytanie jest nullem")
    void getChatGptAnswer_shouldThrowExceptionWhenQuestionIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            springAssistantService.getChatGptAnswer(null);
        });
        assertEquals("Field question is necessary", exception.getMessage());
    }

    @Test
    @DisplayName("Pojawił się wyjątek, gdy pytanie jest puste")
    void getChatGptAnswer_shouldThrowExceptionWhenQuestionIsBlank() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            springAssistantService.getChatGptAnswer(" ");
        });
        assertEquals("Field question can't be empty.", exception.getMessage());
    }

    @Test
    @DisplayName("Pojawił się wyjątek, gdy pytanie jest numerem")
    void getChatGptAnswer_shouldThrowExceptionWhenQuestionIsNumeric() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            springAssistantService.getChatGptAnswer("123456");
        });
        assertEquals("Field question can't be a number.", exception.getMessage());
    }

    @Test
    @DisplayName("Pojawił się wyjątek, gdy pytanie jest zbyt krótkie")
    void getChatGptAnswer_shouldThrowExceptionWhenQuestionIsTooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            springAssistantService.getChatGptAnswer("Short");
        });
        assertEquals("Field question must have minimum 10 letters", exception.getMessage());
    }
}