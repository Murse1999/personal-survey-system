package com.example.personalproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String email;

  @Column(name = "code_hash", nullable = false, length = 100)
  private String codeHash;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "used_at")
  private LocalDateTime usedAt;

  @Column(nullable = false)
  private Integer attempts = 0;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  protected PasswordResetToken() {
  }

  public PasswordResetToken(
    String email,
    String codeHash,
    LocalDateTime expiresAt,
    LocalDateTime createdAt) {
    this.email = email;
    this.codeHash = codeHash;
    this.expiresAt = expiresAt;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getCodeHash() {
    return codeHash;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public LocalDateTime getUsedAt() {
    return usedAt;
  }

  public void setUsedAt(LocalDateTime usedAt) {
    this.usedAt = usedAt;
  }

  public Integer getAttempts() {
    return attempts;
  }

  public void setAttempts(Integer attempts) {
    this.attempts = attempts;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
