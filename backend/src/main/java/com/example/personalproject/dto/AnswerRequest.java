package com.example.personalproject.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AnswerRequest {

  // 使用者回答哪一題
  @NotNull(message = "題目 id 不可為空")
  private Long questionId;

  // 選擇題選了哪些選項
  // 單選題通常只有一個 id
  // 複選題可以有多個 id
  private List<Long> optionIds;

  // 文字題的回答內容
  private String answerText;

  public Long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public List<Long> getOptionIds() {
    return optionIds;
  }

  public void setOptionIds(List<Long> optionIds) {
    this.optionIds = optionIds;
  }

  public String getAnswerText() {
    return answerText;
  }

  public void setAnswerText(String answerText) {
    this.answerText = answerText;
  }
}
