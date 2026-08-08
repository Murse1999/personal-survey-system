package com.example.personalproject.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 接收前端送來的一個選項。
 *
 * 例如：
 * {
 *   "optionCode": "A",
 *   "optionText": "非常同意"
 * }
 */
public class OptionRequest {

  // 選項代號不可為空，例如 A、B、C。
  @NotBlank(message = "選項代號不可為空")
  private String optionCode;

  // 選項內容不可為空。
  @NotBlank(message = "選項內容不可為空")
  private String optionText;

  // Spring/Jackson 建立這個資料盒時需要無參數建構子。
  public OptionRequest() {
  }

  public String getOptionCode() {
    return optionCode;
  }

  public void setOptionCode(String optionCode) {
    this.optionCode = optionCode;
  }

  public String getOptionText() {
    return optionText;
  }

  public void setOptionText(String optionText) {
    this.optionText = optionText;
  }
}
