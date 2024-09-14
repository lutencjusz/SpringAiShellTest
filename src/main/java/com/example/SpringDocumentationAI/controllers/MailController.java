package com.example.SpringDocumentationAI.controllers;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.services.AiUserService;
import com.example.SpringDocumentationAI.services.MailService;
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

    @GetMapping("/confirm-registration")
    public RedirectView getActivateMail(@RequestParam("id") String encryptedKey) throws Exception {
        System.out.println("Potwierdzenie rejestracji: " + AiUserService.decrypt(encryptedKey, mailSecret));
        Optional<DtoUser> user = aiUserService.findByUsername(AiUserService.decrypt(encryptedKey, mailSecret));
        if (user.isPresent()) {
            if (user.get().isEnabled()) {
                return new RedirectView("/login?confirmed");
            }
            DtoUser actualUser = user.get();
            actualUser.setEnabled(true);
            aiUserService.saveUser(actualUser);
            return new RedirectView("/login?activation");
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
            String link = "http://" + appName + "/change-password?id=" + AiUserService.encrypt(actualUser.getUsername(), mailSecret);
            if (mailService.sendEmail(actualUser.getEmail(),
                    "Zmiana hasła w Document AI Analizer",
                    "/templates/change-password-email.html",
                    link)) {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(@RequestParam("id") String username, Model model) throws Exception {
        Optional<DtoUser> user = aiUserService.findByUsername(AiUserService.decrypt(username, mailSecret));
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("email", user.get().getEmail());
        return "change-password";
    }
}

