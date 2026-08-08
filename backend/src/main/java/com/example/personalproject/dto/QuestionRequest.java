package com.example.personalproject.dto;

import com.example.personalproject.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 接收前端送來的一個題目。
 *
 * 一個題目裡面可以包含多個選項。
 */
public class QuestionRequest {

  // 題號不可為空，例如 1、2、3。
  @NotNull(message = "題號不可為空")
  private Integer questionNum;

  // 題目文字不可為空。
  @NotBlank(message = "題目標題不可為空")
  private String title;

  // 題型不可為空，例如 SINGLE、MULTI、TEXT。
  @NotNull(message = "題型不可為空")
  private QuestionType type;

  // 這題是否必填，預設為 true。
  private Boolean isRequired = true;

  /*
   * 一個題目可以有很多個選項。
   *
   * @Valid 的意思是：
   * 題目裡面的每一個 OptionRequest
   * 也要繼續檢查 @NotBlank。
   */
  private List<@Valid OptionRequest> options = new ArrayList<>();

  // Spring/Jackson 建立資料盒時需要無參數建構子。
  public QuestionRequest() {
  }

  public Integer getQuestionNum() {
    return questionNum;
  }

  public void setQuestionNum(Integer questionNum) {
    this.questionNum = questionNum;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public Boolean getIsRequired() {
    return isRequired;
  }

  public void setIsRequired(Boolean required) {
    isRequired = required;
  }

  public List<OptionRequest> getOptions() {
    return options;
  }

  public void setOptions(List<OptionRequest> options) {
    this.options = options;
  }
}
