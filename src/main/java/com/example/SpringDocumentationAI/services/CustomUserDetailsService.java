package com.example.SpringDocumentationAI.services;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.repositories.AiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
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
            throw new UsernameNotFoundException("Użytkownik nie został znaleziony");
        }
        if (!dtoUser.get().isEnabled()) {
            throw new UsernameNotFoundException("Użytkownik nie jest aktywny");
        }
        User user = new User(dtoUser.get().getUsername(), dtoUser.get().getPassword(), dtoUser.get().getAuthorities());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), dtoUser.get().isEnabled(), true, true, true, user.getAuthorities());

    }
}

