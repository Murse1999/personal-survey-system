package com.example.personalproject.repository;

import com.example.personalproject.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository
  extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(
    String email);
}
