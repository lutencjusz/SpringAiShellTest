package com.example.SpringDocumentationAI.controllers.gui;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.services.AiUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AiUserGuiController {

    @Autowired
    private AiUserService aiUserService;

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
    public String registerUser(@Valid @ModelAttribute("user") DtoUser user,  BindingResult result, Model model) {
        // Sprawdzanie błędów walidacji
        if (result.hasErrors()) {
            // Jeżeli są błędy, wracamy do formularza
            return "register-user";
        }

        // Kodowanie hasła, jeśli potrzebne
        // user.setPassword(bcryptPasswordEncoder.encode(user.getPassword()));

        // Zapisujemy użytkownika
        aiUserService.saveUser(user);

        return "redirect:/success"; // Przekierowanie po sukcesie
    }

    // Pokazanie formularza edycji istniejącego użytkownika
    @GetMapping("/edit-user/{username}")
    public String showEditForm(@PathVariable("id") String username, Model model) {
        Optional<DtoUser> user = aiUserService.findByUsername(username);
        if (user.isEmpty()) {
            return "error";
        }
        model.addAttribute("user", user.get());// Przekazanie użytkownika do formularza
        return "register-user"; // Używamy tego samego formularza dla edycji
    }

    @PostMapping("/edit-user")
    public String updateUser(@ModelAttribute("user") DtoUser user) {
        // Kodowanie hasła jeśli hasło zostało zmienione
        // user.setPassword(bcryptPasswordEncoder.encode(user.getPassword()));

        aiUserService.saveUser(user); // Zapisz zmiany użytkownika
        return "redirect:/success";
    }

}
