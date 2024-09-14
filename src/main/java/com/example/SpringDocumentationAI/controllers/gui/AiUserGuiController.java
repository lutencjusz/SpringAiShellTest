package com.example.SpringDocumentationAI.controllers.gui;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.services.AiUserService;
import com.example.SpringDocumentationAI.services.MailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.regex.Pattern;

@Slf4j
@Controller
public class AiUserGuiController {

    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{5,}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private MailService mailService;

    @Value("${MAIL_SECRET}")
    private String mailSecret;

    @GetMapping("/register-user")
    public String registerUser(Model model) {
        // Pobranie aktualnie zalogowanego użytkownika
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Pobranie danych użytkownika (możesz potrzebować dostosować to w zależności od swojej implementacji UserDetails)
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Dodanie zalogowanego użytkownika do modelu
            DtoUser currentUser = new DtoUser();
            currentUser.setUsername(userDetails.getUsername());
            currentUser.setPassword(userDetails.getPassword());
            if (userDetails.getAuthorities().stream().findFirst().get().getAuthority().contains("ADMIN")) {
                currentUser.setRole("ADMIN");
            } else {
                currentUser.setRole("USER");
            }
            model.addAttribute("user", currentUser);
        } else {
            // W przypadku gdy użytkownik nie jest zalogowany, inicjalizujemy pusty obiekt
            model.addAttribute("user", new DtoUser());
        }

        return "register-user"; // Wyświetlenie formularza
    }

    // Obsługa wysłania formularza
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") DtoUser user, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("error", "Wystąpił błąd podczas rejestracji: " + result.getAllErrors());
        } else if (aiUserService.findByUsername(user.getUsername()).isPresent()) {
            DtoUser existingUser = aiUserService.findByUsername(user.getUsername()).get();
            model.addAttribute("error", "Użytkownik o podanej nazwie już istnieje");
        } else if (aiUserService.findByEmail(user.getEmail()).isPresent()) {
            DtoUser existingUser = aiUserService.findByEmail(user.getEmail()).get();
            model.addAttribute("error", "Użytkownik o podanym adresie email już istnieje");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            model.addAttribute("error", "Email ma nieprawidłowy format");
        } else if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            model.addAttribute("error", "Hasło jest zbyt słabe. Musi zawierać co najmniej 5 znaków, jedną dużą literę, cyfrę i znak specjalny");
        }
        if (model.containsAttribute("error")) {
            return "register-user";
        }
        String appName = System.getenv("HEROKU_APP_NAME");
        if (appName == null) {
            appName = "localhost:8080";
        }
        String link = "http://" + appName + "/confirm-registration?id=" + AiUserService.encrypt(user.getUsername(), mailSecret);
        if (mailService.sendEmail(user.getEmail(),
                "Potwierdzenie rejestracji Document AI Analizer",
                "/templates/welcome-email.html",
                link)) {
            log.info("Wysłano email z potwierdzeniem rejestracji");
            aiUserService.saveUserAndEncodePass(user);
            model.addAttribute("email", user.getEmail());
            return "register-success"; // Przekierowanie po sukcesie
        } else {
            log.error("Błąd podczas wysyłania emaila z potwierdzeniem");
            model.addAttribute("messageType", "error");
            model.addAttribute("message", "Błąd podczas wysyłania emaila z potwierdzeniem");
            return "register-user";
        }
    }
}
