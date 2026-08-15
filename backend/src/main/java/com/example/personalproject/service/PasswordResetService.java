package com.example.personalproject.service;

import com.example.personalproject.dto.PasswordResetConfirmRequest;
import com.example.personalproject.dto.PasswordResetRequest;
import com.example.personalproject.dto.PasswordResetResponse;
import com.example.personalproject.entity.PasswordResetToken;
import com.example.personalproject.entity.User;
import com.example.personalproject.repository.PasswordResetTokenRepository;
import com.example.personalproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class PasswordResetService {

  private static final String GENERIC_MESSAGE =
    "如果這個 Email 已註冊，驗證碼會寄到你的信箱";
  private static final String INVALID_CODE_MESSAGE = "驗證碼錯誤或已過期";
  private static final int MAX_ATTEMPTS = 5;
  private static final int CODE_EXPIRATION_MINUTES = 10;
  private static final int REQUEST_COOLDOWN_SECONDS = 60;

  private final UserRepository userRepository;
  private final PasswordResetTokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final PasswordResetEmailService emailService;
  private final SecureRandom secureRandom = new SecureRandom();

  public PasswordResetService(
    UserRepository userRepository,
    PasswordResetTokenRepository tokenRepository,
    PasswordEncoder passwordEncoder,
    PasswordResetEmailService emailService) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }

  @Transactional
  public PasswordResetResponse requestCode(PasswordResetRequest request) {
    String email = normalizeEmail(request.getEmail());
    User user = userRepository.findByEmail(email).orElse(null);

    if (user == null) {
      return new PasswordResetResponse(GENERIC_MESSAGE);
    }

    LocalDateTime now = LocalDateTime.now();
    PasswordResetToken latestToken = tokenRepository
      .findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(email)
      .orElse(null);

    if (latestToken != null
      && latestToken.getCreatedAt()
        .plusSeconds(REQUEST_COOLDOWN_SECONDS)
        .isAfter(now)) {
      throw new IllegalArgumentException("請稍後再重新取得驗證碼");
    }

    String code = String.format(
      Locale.ROOT,
      "%06d",
      secureRandom.nextInt(1_000_000)
    );
    PasswordResetToken token = new PasswordResetToken(
      email,
      passwordEncoder.encode(code),
      now.plusMinutes(CODE_EXPIRATION_MINUTES),
      now
    );

    tokenRepository.save(token);
    String deliveryMessage = emailService.sendPasswordResetCode(email, code);

    return new PasswordResetResponse(deliveryMessage);
  }

  @Transactional
  public PasswordResetResponse resetPassword(
    PasswordResetConfirmRequest request) {
    String email = normalizeEmail(request.getEmail());
    PasswordResetToken token = tokenRepository
      .findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(email)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_CODE_MESSAGE));

    LocalDateTime now = LocalDateTime.now();
    if (token.getExpiresAt().isBefore(now)
      || token.getAttempts() >= MAX_ATTEMPTS) {
      throw new IllegalArgumentException(INVALID_CODE_MESSAGE);
    }

    if (!passwordEncoder.matches(request.getCode(), token.getCodeHash())) {
      token.setAttempts(token.getAttempts() + 1);
      tokenRepository.save(token);
      throw new IllegalArgumentException(INVALID_CODE_MESSAGE);
    }

    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException(INVALID_CODE_MESSAGE));
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    token.setUsedAt(now);
    tokenRepository.save(token);

    return new PasswordResetResponse("密碼已更新，請使用新密碼登入");
  }

  private String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }
}
