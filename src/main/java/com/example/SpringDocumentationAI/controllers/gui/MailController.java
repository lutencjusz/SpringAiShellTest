package com.example.SpringDocumentationAI.controllers.gui;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.services.AiUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class MailController {

    @Autowired
    private AiUserService aiUserService;

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
}

