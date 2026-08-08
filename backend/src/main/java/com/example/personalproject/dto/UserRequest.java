package com.example.personalproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class UserRequest {

  @NotBlank(message = "姓名不可為空")
  private String name;
  @NotBlank(message = "電話不可為空")
  private String phone;
  @NotBlank(message = "密碼不可為空")
  private String password;
  @NotBlank(message = "Email不可為空")
  @Email(message = "Email 格式錯誤")
  private String email;
  @Min(value = 0, message = "年齡不可小於 0")
  private Integer age;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }
}
