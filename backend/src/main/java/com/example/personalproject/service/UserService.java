package com.example.personalproject.service;

import com.example.personalproject.dto.LoginRequest;
import com.example.personalproject.dto.LoginResponse;
import com.example.personalproject.dto.UserRequest;
import com.example.personalproject.entity.User;
import com.example.personalproject.config.JwtService;
import com.example.personalproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
      foundUser.getRole()
    );
  }


}
