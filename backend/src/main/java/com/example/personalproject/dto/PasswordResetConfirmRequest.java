package com.example.personalproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordResetConfirmRequest {

  @NotBlank(message = "Email 不可為空")
  @Email(message = "Email 格式錯誤")
  private String email;

  @NotBlank(message = "驗證碼不可為空")
  @Pattern(regexp = "\\d{6}", message = "驗證碼必須是 6 位數字")
  private String code;

  @NotBlank(message = "新密碼不可為空")
  @Size(min = 8, max = 72, message = "新密碼長度必須為 8 到 72 個字元")
  private String newPassword;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
