package com.example.personalproject.dto;

/**
 * 回傳給前端的一個選項。
 *
 * 例如：
 * {
 *   "id": 1,
 *   "optionCode": "A",
 *   "optionText": "蘋果"
 * }
 */
public class OptionResponse {

  // 資料庫產生的選項 id。
  private Long id;

  // 選項代號，例如 A、B、C。
  private String optionCode;

  // 選項內容，例如 蘋果、香蕉。
  private String optionText;

  // 建立空的回傳資料盒。
  public OptionResponse() {
  }

  // 建立一個完整的回傳資料盒。
  public OptionResponse(
    Long id,
    String optionCode,
    String optionText) {

    this.id = id;
    this.optionCode = optionCode;
    this.optionText = optionText;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
