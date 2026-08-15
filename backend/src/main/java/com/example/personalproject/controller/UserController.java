package com.example.personalproject.controller;

import com.example.personalproject.dto.LoginRequest;
import com.example.personalproject.dto.LoginResponse;
import com.example.personalproject.dto.UserRequest;
import com.example.personalproject.dto.UserProfileResponse;
import com.example.personalproject.dto.UserProfileUpdateRequest;
import com.example.personalproject.dto.PasswordResetConfirmRequest;
import com.example.personalproject.dto.PasswordResetRequest;
import com.example.personalproject.dto.PasswordResetResponse;
import com.example.personalproject.service.UserService;
import com.example.personalproject.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final PasswordResetService passwordResetService;

  public UserController(
    UserService userService,
    PasswordResetService passwordResetService) {
    this.userService = userService;
    this.passwordResetService = passwordResetService;
  }

  @PostMapping
  public Long createUser(@RequestBody @Valid UserRequest request){
    return userService.addUser(request);
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody @Valid LoginRequest request){
    return userService.login(request);
  }

  @PostMapping("/password-reset/request")
  public PasswordResetResponse requestPasswordReset(
    @RequestBody @Valid PasswordResetRequest request) {
    return passwordResetService.requestCode(request);
  }

  @PostMapping("/password-reset/confirm")
  public PasswordResetResponse confirmPasswordReset(
    @RequestBody @Valid PasswordResetConfirmRequest request) {
    return passwordResetService.resetPassword(request);
  }

  @GetMapping("/me")
  public UserProfileResponse getMyProfile(Authentication authentication) {
    return userService.getProfile(authentication.getName());
  }

  @PutMapping("/me")
  public UserProfileResponse updateMyProfile(
    @RequestBody @Valid UserProfileUpdateRequest request,
    Authentication authentication) {

    return userService.updateProfile(authentication.getName(), request);
  }

}
