package com.example.personalproject.service;

import com.example.personalproject.dto.LoginRequest;
import com.example.personalproject.dto.LoginResponse;
import com.example.personalproject.dto.UserRequest;
import com.example.personalproject.dto.UserProfileResponse;
import com.example.personalproject.dto.UserProfileUpdateRequest;
import com.example.personalproject.entity.User;
import com.example.personalproject.config.JwtService;
import com.example.personalproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public UserService(
    UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    JwtService jwtService
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public Long addUser(UserRequest request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email 已經存在");
    }
    String encodedPassword =
      passwordEncoder.encode(request.getPassword());

    User user = new User(
      request.getName(),
      request.getPhone(),
      encodedPassword,
      request.getEmail(),
      request.getAge()
    );
    // 舊版客戶端沒有傳頭像時，沿用資料庫預設的 MALE；新註冊頁會明確傳入選項。
    if (request.getAvatarType() != null) {
      user.setAvatarType(normalizeAvatarType(request.getAvatarType()));
    }
    User savedUser = userRepository.save(user);
    return savedUser.getId();

  }

  public LoginResponse login(LoginRequest request) {

    Optional<User> user = userRepository.findByEmail(request.getEmail());
    if (user.isEmpty()) {
      throw new IllegalArgumentException("使用者不存在");
    }
    User foundUser = user.get();

    if (!passwordEncoder.matches(request.getPassword(),foundUser.getPassword())) {
      throw new IllegalArgumentException("密碼錯誤");
    }

    String token = jwtService.generateToken(foundUser);
    return new LoginResponse(
      token,
      foundUser.getEmail(),
      foundUser.getRole(),
      foundUser.getName(),
      foundUser.getPhone(),
      foundUser.getAge(),
      foundUser.getAvatarType()
    );
  }

  @Transactional(readOnly = true)
  public UserProfileResponse getProfile(String email) {
    return toProfile(findUser(email));
  }

  @Transactional
  public UserProfileResponse updateProfile(
    String email,
    UserProfileUpdateRequest request) {

    User user = findUser(email);
    user.setName(request.getName().trim());
    user.setPhone(request.getPhone().trim());
    user.setAge(request.getAge());
    user.setAvatarType(normalizeAvatarType(request.getAvatarType()));

    return toProfile(userRepository.save(user));
  }

  private User findUser(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("使用者不存在"));
  }

  private UserProfileResponse toProfile(User user) {
    return new UserProfileResponse(
      user.getId(),
      user.getName(),
      user.getPhone(),
      user.getEmail(),
      user.getAge(),
      user.getAvatarType(),
      user.getRole()
    );
  }

  private String normalizeAvatarType(String avatarType) {
    String normalized = avatarType == null
      ? ""
      : avatarType.trim().toUpperCase(Locale.ROOT);

    if (!normalized.equals("MALE") && !normalized.equals("FEMALE")) {
      throw new IllegalArgumentException("頭像只能選擇男生或女生");
    }

    return normalized;
  }


}
