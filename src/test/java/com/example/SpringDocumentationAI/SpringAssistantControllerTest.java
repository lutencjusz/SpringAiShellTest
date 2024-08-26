package com.example.SpringDocumentationAI;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpringAssistantControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private SpringAssistantService springAssistantService;

    @InjectMocks
    private SpringAssistantController springAssistantController;

    @Test
    void shouldAnswerForQuestionFromChatGpt() throws Exception {


        Mockito.when(springAssistantService.getChatGptAnswer("Czy kury są szczęśliwe?"))
                .thenReturn("Nie");

        mockMvc.perform(MockMvcRequestBuilders.post("/question")
                        .content("{\"id\":\"1\",\"question\":\"Czy kury są szczęśliwe?\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Nie"));
    }
}