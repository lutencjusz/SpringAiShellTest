package com.example.SpringDocumentationAI;

import com.example.SpringDocumentationAI.services.AiUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private AiUserService aiUserService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String username = request.getParameter("username");

        try {
            UserDetails userDetails = aiUserService.loadUserByUsername(username);

            // Sprawdź czy flaga enable jest ustawiona na false
            if (!userDetails.isEnabled()) {
                response.sendRedirect("/login?disable");
                return;
            }
        } catch (UsernameNotFoundException e) {
            // Użytkownik nie istnieje lub inny problem
            response.sendRedirect("/login?error");
            return;
        }

        if (exception instanceof DisabledException) {
            response.sendRedirect("/login?disable");
        } else if (exception instanceof LockedException) {
            response.sendRedirect("/login?locked");
        } else {
            // Domyślne zachowanie, gdy inne wyjątki uwierzytelniania
            response.sendRedirect("/login?error");
        }
    }
}



