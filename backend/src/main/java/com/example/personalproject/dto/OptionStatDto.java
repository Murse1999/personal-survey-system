package com.example.personalproject.dto;

import java.math.BigDecimal;

/**
 * 單一選項的統計資料。
 */
public class OptionStatDto {

  // 選項 ID。
  private Long optionId;

  // 選項代碼，例如 A、B、C。
  private String optionCode;

  // 選項顯示文字。
  private String optionText;

  // 這個選項被選取的次數。
  private long selectedCount;

  // 這個選項占全部填答者的百分比。
  private BigDecimal percentage;

  // Jackson 建立回傳物件時需要無參數建構子。
  public OptionStatDto() {
  }

  public OptionStatDto(
    Long optionId,
    String optionCode,
    String optionText,
    long selectedCount,
    BigDecimal percentage) {

    this.optionId = optionId;
    this.optionCode = optionCode;
    this.optionText = optionText;
    this.selectedCount = selectedCount;
    this.percentage = percentage;
  }

  public Long getOptionId() {
    return optionId;
  }

  public void setOptionId(Long optionId) {
    this.optionId = optionId;
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

  public long getSelectedCount() {
    return selectedCount;
  }

  public void setSelectedCount(long selectedCount) {
    this.selectedCount = selectedCount;
  }

  public BigDecimal getPercentage() {
    return percentage;
  }

  public void setPercentage(BigDecimal percentage) {
    this.percentage = percentage;
  }
}
