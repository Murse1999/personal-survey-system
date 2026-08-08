package com.example.personalproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;

/**
 * 接收前端送來的「建立問卷資料」。
 *
 * DTO 不是資料庫表格，
 * 它只是前端和後端之間傳送資料的盒子。
 */
public class QuizRequest {

  // 問卷標題不可為空，也不可只輸入空白。
  @NotBlank(message = "問卷標題不可為空")
  private String title;

  // 問卷說明可以不填。
  private String description;

  // 開始時間不可為空。
  @NotNull(message = "開始時間不可為空")
  private LocalDateTime startDate;

  // 結束時間不可為空。
  @NotNull(message = "結束時間不可為空")
  private LocalDateTime endDate;

  // 如果前端沒有傳，預設為 false。
  private Boolean isPublished = false;

  private List<@Valid QuestionRequest> questions;

  // Spring/Jackson 建立物件時需要無參數建構子。
  public QuizRequest() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public Boolean getIsPublished() {
    return isPublished;
  }

  public void setIsPublished(Boolean published) {
    isPublished = published;
  }

  public List<QuestionRequest> getQuestions() {
    return questions;
  }

  public void setQuestions(List<QuestionRequest> questions) {
    this.questions = questions;
  }
}
