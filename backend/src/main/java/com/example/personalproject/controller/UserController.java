package com.example.personalproject.controller;

import com.example.personalproject.dto.LoginRequest;
import com.example.personalproject.dto.LoginResponse;
import com.example.personalproject.dto.UserRequest;
import com.example.personalproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public Long createUser(@RequestBody @Valid UserRequest request){
    return userService.addUser(request);
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody @Valid LoginRequest request){
    return userService.login(request);
  }

}
