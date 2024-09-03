package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.model.DtoChatGptRequest;
import com.example.SpringDocumentationAI.model.JwtLoginForm;
import com.example.SpringDocumentationAI.services.SpringAssistantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureOrder
@AutoConfigureMockMvc
@ActiveProfiles("Test")
class SpringAssistantControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private SpringAssistantService springAssistantService;

    private static final DtoChatGptRequest dtoChatGptRequest = new DtoChatGptRequest("1", "Czy kury są szczęśliwe?");
    private static final JwtLoginForm jwtLoginForm = new JwtLoginForm("admin", "admin123#");

    private static String jwtKey = "";

    private String getJwtKey() throws Exception {
        if (jwtKey.isEmpty()) {
            jwtKey = mockMvc.perform(post("/authenticate")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(jwtLoginForm)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
        }
        log.info("JWT key: {}", jwtKey);
        return jwtKey;
    }

    @Test
    @DisplayName("Powinien zwrócić prawidłową odpowiedź na pytanie z ChatGpt")
    void shouldAnswerForQuestionFromChatGpt() throws Exception {
        ResponseEntity<String> answer = ResponseEntity.ok("Nie");

        Mockito.when(springAssistantService.getChatGptAnswer(dtoChatGptRequest.getQuestion()))
                .thenReturn(String.valueOf(answer));

        String response = mockMvc.perform(post("/question")
                        .header("Authorization", "Bearer " + getJwtKey())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dtoChatGptRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertThat(response).contains("Nie");
    }
}