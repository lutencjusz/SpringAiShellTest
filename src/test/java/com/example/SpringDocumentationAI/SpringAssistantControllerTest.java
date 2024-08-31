package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.model.DtoChatGptRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SpringAssistantControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private SpringAssistantService springAssistantService;

    @Test
    void shouldAnswerForQuestionFromChatGpt() throws Exception {
        DtoChatGptRequest dtoChatGptRequest = new DtoChatGptRequest();
        dtoChatGptRequest.setId("1");
        dtoChatGptRequest.setQuestion("Czy kury są szczęśliwe?");
        ResponseEntity<String> answer = ResponseEntity.ok("Nie");

        Mockito.when(springAssistantService.getChatGptAnswer(dtoChatGptRequest.getQuestion()))
                .thenReturn(String.valueOf(answer));

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/question")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dtoChatGptRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertThat(response).contains("Nie");
    }
}