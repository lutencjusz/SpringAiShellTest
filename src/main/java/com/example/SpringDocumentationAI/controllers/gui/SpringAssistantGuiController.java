package com.example.SpringDocumentationAI.controllers.gui;

import com.example.SpringDocumentationAI.MessagePropertiesGenerator;
import com.example.SpringDocumentationAI.services.SpringAssistantService;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Slf4j
@Controller
public class SpringAssistantGuiController {

    @Autowired
    private SpringAssistantService springAssistantService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    MessagePropertiesGenerator messagePropertiesGenerator;

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
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", authentication.getAuthorities().stream().findFirst().get().getAuthority());
        return "index";
    }

    @HxRequest
    @PostMapping("/api/chat")
    public HtmxResponse generate(HttpServletRequest request, Model model) throws UnknownHostException {
        String message = request.getParameter("message");
        String inetAddress = request.getRemoteAddr();
        log.info("Otrzymany komunikat: {}", message);
//        if (inetAddress.equals("0:0:0:0:0:0:0:1")) {
//            inetAddress = InetAddress.getLocalHost().getHostAddress();
//        }
//        if (!inetAddress.contains("192.168.")) {
//            throw new UnknownHostException("Unknow host '" + inetAddress + "' outside of local network");
//        }
        Instant startTime = Instant.now();
        String response = springAssistantService.getChatGptAnswer(message);
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Instant endTime = Instant.now();// Zakończ mierzenie czasu
        Duration duration = Duration.between(startTime, endTime);
        long timeElapsed = duration.toMillis();
        log.info("Adres odbiorcy: '{}', Odpowiedź: {}", inetAddress, response);
        model.addAttribute("response", renderer.render(parser.parse(response)));
        model.addAttribute("message", message);
        model.addAttribute("timeElapsed", String.valueOf(timeElapsed));
        return HtmxResponse.builder()
                .view("response :: responseFragment")
                .view("recent-message-list :: messageFragment")
                .build();
    }

    @GetMapping("/login")
    public String customLogin(Model model) throws IOException {
//        Locale.setDefault(Locale.ENGLISH);
        String countryCode = String.valueOf(Locale.getDefault());
        log.info("Kod kraju: {}", countryCode);
        messagePropertiesGenerator.createPropertiesFile(countryCode);
        model.addAttribute("message_title", messageSource.getMessage("login.title", null, Locale.getDefault()));
        model.addAttribute("message_username", messageSource.getMessage("login.username", null, Locale.getDefault()));
        model.addAttribute("message_password", messageSource.getMessage("login.password", null, Locale.getDefault()));
        model.addAttribute("message_password_forgot", messageSource.getMessage("login.password.forgot", null, Locale.getDefault()));
        model.addAttribute("message_submit", messageSource.getMessage("login.submit", null, Locale.getDefault()));
        model.addAttribute("message_not_registered", messageSource.getMessage("login.not.registered", null, Locale.getDefault()));
        model.addAttribute("message_register", messageSource.getMessage("login.register", null, Locale.getDefault()));
        model.addAttribute("message_or", messageSource.getMessage("login.or", null, Locale.getDefault()));
        model.addAttribute("message_email", messageSource.getMessage("login.email", null, Locale.getDefault()));
        model.addAttribute("message_send_email", messageSource.getMessage("login.send.email", null, Locale.getDefault()));
        return "login";
    }

    @GetMapping("/set-locate")
    public ResponseEntity<String> setLocale(@RequestParam(name = "lang") Locale locale) {
        log.info("Ustawiono lokalizację na: {}", locale);
        Locale.setDefault(locale);
        return ResponseEntity.ok("");
    }

}
