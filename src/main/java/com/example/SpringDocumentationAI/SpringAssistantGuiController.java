package com.example.SpringDocumentationAI;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.logging.Logger;

@Controller
@CrossOrigin
public class SpringAssistantGuiController {

    @Autowired
    private SpringAssistantService springAssistantService;

    private static final Logger logger = Logger.getLogger(SpringAssistantGuiController.class.getName());
    private final ChatClient chatClient;

    /**
     * Konstruktor klasy springassistantguicontroller, który tworzy obiekt klasy 'ChatClient.Builder' i ustawia go tak,
     * aby pamiętał poprzednie wiadomości.
     *
     * @param chatClient - obiekt klasy ChatClient, który będzie używany do komunikacji z serwerem.
     */
    public SpringAssistantGuiController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @HxRequest
    @PostMapping("/api/chat")
    public HtmxResponse generate(@RequestParam String message, Model model) {
        logger.info("Otrzymany komunikat: " + message);
        String response = springAssistantService.getChatGptAnswer(message);
        logger.info("Odpowiedź: " + response);
        model.addAttribute("response", response);
        model.addAttribute("message", message);
        return HtmxResponse.builder()
                .view("response :: responseFragment")
                .view("recent-message-list :: messageFragment")
                .build();
    }

}
