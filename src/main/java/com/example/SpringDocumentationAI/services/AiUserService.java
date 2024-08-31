package com.example.SpringDocumentationAI.services;

import com.example.SpringDocumentationAI.model.AiUser;
import com.example.SpringDocumentationAI.repositories.AiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AiUserService implements UserDetailsService {

    @Autowired
    private AiUserRepository aiUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AiUser> actualUser = aiUserRepository.findByUsername(username);
        if (actualUser.isPresent()) {
            var userObject = actualUser.get();
            return User.builder()
                    .username(userObject.getUsername())
                    .password(userObject.getPassword())
                    .roles(getRoles(userObject))
                    .build();
        } else {
            throw new UsernameNotFoundException("Użytkownik nie istnieje");
        }
    }

    private String[] getRoles(AiUser aiUser) {
        if (aiUser.getRole() == null) {
            return new String[]{"ADMIN"};
        }
        return new String[]{aiUser.getRole()};
    }

    // Metoda do dodawania nowego użytkownika
    public AiUser saveUser(AiUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Szyfruj hasło przed zapisem
        return aiUserRepository.save(user);
    }
}
