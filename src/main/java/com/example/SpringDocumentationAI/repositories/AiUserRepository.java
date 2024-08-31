package com.example.SpringDocumentationAI.repositories;

import com.example.SpringDocumentationAI.model.AiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiUserRepository extends JpaRepository<AiUser, Long> {
    Optional<AiUser> findByUsername(String username);
}
