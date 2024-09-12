package com.example.SpringDocumentationAI.repositories;

import com.example.SpringDocumentationAI.model.DtoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiUserRepository extends JpaRepository<DtoUser, Long> {
    Optional<DtoUser> findByUsername(String username);
    Optional<DtoUser> findByEmail(String email);
}
