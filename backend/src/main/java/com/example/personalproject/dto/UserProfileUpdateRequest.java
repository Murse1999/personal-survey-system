package com.example.personalproject.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** 使用者可以修改的個人資料；email、角色和密碼不在這個表單裡。 */
public class UserProfileUpdateRequest {

    @NotBlank(message = "姓名不可為空")
    private String name;

    @NotBlank(message = "電話不可為空")
    private String phone;

    @Min(value = 0, message = "年齡不可小於 0")
    @Max(value = 120, message = "年齡不可大於 120")
    private Integer age;

    @NotBlank(message = "請選擇頭像")
    private String avatarType;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getAvatarType() { return avatarType; }
    public void setAvatarType(String avatarType) { this.avatarType = avatarType; }
}
