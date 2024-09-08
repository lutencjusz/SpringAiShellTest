package com.example.SpringDocumentationAI.controllers.gui;

import com.example.SpringDocumentationAI.services.SpringAssistantService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Controller
@CrossOrigin
public class SpringAssistantGuiController {

    @Autowired
    private SpringAssistantService springAssistantService;

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

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @HxRequest
    @PostMapping("/api/chat")
    public HtmxResponse generate(HttpServletRequest request, Model model) throws UnknownHostException {
        String message = request.getParameter("message");
        String inetAddress = request.getRemoteAddr();
        log.info("Otrzymany komunikat: {}", message);
        if (inetAddress.equals("0:0:0:0:0:0:0:1")) {
            inetAddress = InetAddress.getLocalHost().getHostAddress();
        }
        if (!inetAddress.contains("192.168.")) {
            throw new UnknownHostException("Unknow host '" + inetAddress + "' outside of local network");
        }
        Instant startTime = Instant.now();
        String response = springAssistantService.getChatGptAnswer(message);
        Instant endTime = Instant.now();// Zakończ mierzenie czasu
        Duration duration = Duration.between(startTime, endTime);
        long timeElapsed = duration.toMillis();
        log.info("Adres odbiorcy: '{}', Odpowiedź: {}", inetAddress, response);
        model.addAttribute("response", response);
        model.addAttribute("message", message);
        model.addAttribute("timeElapsed", String.valueOf(timeElapsed));
        return HtmxResponse.builder()
                .view("response :: responseFragment")
                .view("recent-message-list :: messageFragment")
                .build();
    }

    @GetMapping("/login")
    public String customLogin() {
        return "login";
    }

}
