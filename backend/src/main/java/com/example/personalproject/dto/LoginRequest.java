package com.example.personalproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

  @NotBlank(message = "不能空白")
  @Email(message = "請填入正確格式")
  private String email;
  @NotBlank(message = "密碼不可為空")
  private String password;

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
