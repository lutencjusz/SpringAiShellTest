package com.example.SpringDocumentationAI.controllers;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.services.AiUserService;
import com.example.SpringDocumentationAI.services.MailService;
import groovy.lang.Singleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class MailController {

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private MailService mailService;

    @Value("${MAIL_SECRET}")
    private String mailSecret;

    @Value("${HEROKU_APP_NAME}")
    private String appName;

    /**
     * Odebranie linka z potwierdzeniem weryfikacji rejestracji przez administratora
     * Wysyłanie maila z linkiem do potwierdzenia rejestracji przez użytkownika końcowego
     *
     * @param encrypyedUsername - zaszyfrowana nazwa użytkownika
     * @param model - model danych
     * @return - widok formularza potwierdzenia rejestracji przez użytkownika końcowego
     * @throws Exception - wyjątek w przypadku błędu
     */
    @GetMapping("/user-verification")
    public String showUserVerificationForm(@RequestParam("id") String encrypyedUsername, Model model) throws Exception {
        Optional<DtoUser> user = aiUserService.findByUsername(AiUserService.decrypt(encrypyedUsername, mailSecret));
        if (user.isEmpty()) {
            return "redirect:/login:unconfirmed";
        }
        String linkUserConfirmation = "http://" + appName + "/confirm-registration?id=" + AiUserService.encrypt(user.get().getEmail(), mailSecret);
        if (mailService.sendEmail(user.get().getEmail(),
                "Potwierdzenie rejestracji w Document AI Analizer",
                "/templates/welcome-email.html",
                linkUserConfirmation,null)) {
            model.addAttribute("email", user.get().getEmail());
            return "user-confirmation";
        } else {
            return "user-confirmation:error";
        }
    }

    /**
     * Odebranie linka z potwierdzeniem rejestracji przez użytkownika końcowego po weryfikacji przez administratora
     * Wejście na stronę logowania z komunikatem o potwierdzeniu rejestracji
     *
     * @param encryptedEmail - zaszyfrowana nazwa użytkownika
     * @return - przekierowanie do strony logowania z komunikatem o potwierdzeniu rejestracji
     * @throws Exception- wyjątek w przypadku błędu
     */
    @GetMapping("/confirm-registration")
    public RedirectView getActivateMail(@RequestParam("id") String encryptedEmail) throws Exception {
        System.out.println("Potwierdzenie rejestracji: " + AiUserService.decrypt(encryptedEmail, mailSecret));
        Optional<DtoUser> user = aiUserService.findByEmail(AiUserService.decrypt(encryptedEmail, mailSecret));
        if (user.isPresent()) {
            if (user.get().isEnabled()) {
                return new RedirectView("/login?confirmed");
            }
            DtoUser actualUser = user.get();
            actualUser.setEnabled(true);
            aiUserService.saveUser(actualUser);
            return new RedirectView("/login?registersuccess");
        } else {
            return new RedirectView("/login?unconfirmed");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Boolean> getResetPassword(@RequestParam("email") String email) throws Exception {
        Optional<DtoUser> user = aiUserService.findByEmail(email);
        if (user.isPresent()) {
            DtoUser actualUser = user.get();
            String appName = System.getenv("HEROKU_APP_NAME");
            if (appName == null) {
                appName = "localhost:8080";
            }
            String linkResetPassword = "http://" + appName + "/change-password?id=" + AiUserService.encrypt(actualUser.getUsername(), mailSecret);
            if (mailService.sendEmail(actualUser.getEmail(),
                    "Zmiana hasła w Document AI Analizer",
                    "/templates/change-password-email.html",
                    linkResetPassword, null)) {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(@RequestParam("id") String encrypyedUsername, Model model) throws Exception {
        Optional<DtoUser> user = aiUserService.findByUsername(AiUserService.decrypt(encrypyedUsername, mailSecret));
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("email", user.get().getEmail());
        return "change-password";
    }
}

