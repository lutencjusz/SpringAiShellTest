package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.model.DtoChatGptRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SpringAssistantController {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private SpringAssistantService springAssistantService;

    @InjectMocks
    private SpringAssistantController springAssistantController;

    @Test
    void shouldAnswerForQuestionFromChatGpt() throws Exception {
        DtoChatGptRequest dtoChatGptRequest = new DtoChatGptRequest();
        dtoChatGptRequest.setId("1");
        dtoChatGptRequest.setQuestion("Czy kury są szczęśliwe?");
        String answer = "Nie";

        Mockito.when(springAssistantService.getChatGptAnswer(dtoChatGptRequest.getQuestion()))
                .thenReturn(answer);

        mockMvc.perform(MockMvcRequestBuilders.post("/question")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dtoChatGptRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Nie"));
    }
}