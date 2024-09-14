package com.example.SpringDocumentationAI.controllers.gui;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.services.AiUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ChangePasswordGuiController {

    @Autowired
    AiUserService aiUserService;

    @Value("${MAIL_SECRET}")
    private String mailSecret;

    // Obsługa zmiany hasła
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("email") String email,
                                 @RequestParam("password") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) throws Exception {
        Optional<DtoUser> user = aiUserService.findByEmail(email);
        if (user.isPresent()) {
            DtoUser actualUser = user.get();
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Hasła nie są takie same!");
                return "redirect:/change-password?id=" + AiUserService.encrypt(actualUser.getUsername(), mailSecret);
            }
            actualUser.setPassword(newPassword);
            boolean isPasswordChanged = changeUserPassword(actualUser);
            if (!isPasswordChanged) {
                redirectAttributes.addFlashAttribute("error", "Zmiana hasła nie powiodła się. Spróbuj ponownie.");
                return "redirect:/change-password?id=" + AiUserService.encrypt(actualUser.getUsername(), mailSecret);
            }
            return "redirect:/login?passwordChanged";
        }
        redirectAttributes.addFlashAttribute("error", "Użytkownik nie został znaleziony.");
        return "redirect:/login?error";
    }

    private boolean changeUserPassword(DtoUser user) {
        aiUserService.saveUserAndEncodePass(user);
        return true;
    }
}
