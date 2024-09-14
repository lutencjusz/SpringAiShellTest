package com.example.SpringDocumentationAI.services;

import com.example.SpringDocumentationAI.model.DtoUser;
import com.example.SpringDocumentationAI.repositories.AiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;
import java.util.Optional;

@Service
public class AiUserService implements UserDetailsService {

    @Autowired
    private AiUserRepository aiUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<DtoUser> actualUser = aiUserRepository.findByUsername(username);
        if (actualUser.isPresent()) {
            var userObject = actualUser.get();
            return User.builder()
                    .username(userObject.getUsername())
                    .password(userObject.getPassword())
                    .roles(getRoles(userObject))
                    .accountLocked(!userObject.isAccountNonLocked())
                    .disabled(!userObject.isEnabled())
                    .build();
        } else {
            throw new UsernameNotFoundException("Użytkownik nie istnieje");
        }
    }

    private String[] getRoles(DtoUser dtoUser) {
        if (dtoUser.getRole() == null) {
            return new String[]{"ADMIN"};
        }
        return new String[]{dtoUser.getRole()};
    }

    public Optional<DtoUser> findByUsername(String username) {
        return aiUserRepository.findByUsername(username);
    }

    public Optional<DtoUser> findByEmail(String email) {
        return aiUserRepository.findByEmail(email);
    }

    // Metoda do dodawania nowego użytkownika
    public DtoUser saveUser(DtoUser user) {// Szyfruj hasło przed zapisem
        return aiUserRepository.save(user);
    }

    public DtoUser saveUserAndEncodePass(DtoUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Szyfruj hasło przed zapisem
        return aiUserRepository.save(user);
    }

    public static String encrypt(String data, String secretKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        Base32 base32 = new Base32();
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return base32.encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData, String secretKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        Base32 base32 = new Base32();
        byte[] decodedData = base32.decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }
}
