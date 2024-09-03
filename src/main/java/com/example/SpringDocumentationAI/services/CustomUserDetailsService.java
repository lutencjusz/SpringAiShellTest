package com.example.SpringDocumentationAI.services;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.repositories.AiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AiUserRepository aiUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<DtoUser> dtoUser = aiUserRepository.findByUsername(username);
        if (dtoUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        // Konwersja własnej encji User na UserDetails Spring Security
        return new org.springframework.security.core.userdetails.User(dtoUser.get().getUsername(), dtoUser.get().getPassword(), dtoUser.get().getAuthorities());
    }
}

