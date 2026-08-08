package com.example.personalproject.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 回傳給前端的一整份問卷。
 *
 * 裡面可以包含：
 * 問卷基本資料、題目、選項。
 */
public class QuizResponseDto {

  // 問卷 id。
  private Long id;

  // 問卷標題。
  private String title;

  // 問卷說明。
  private String description;

  // 問卷開始時間。
  private LocalDateTime startDate;

  // 問卷結束時間。
  private LocalDateTime endDate;

  // 問卷是否已發布。
  private Boolean isPublished;

  // 問卷裡面的所有題目。
  private List<QuestionResponse> questions = new ArrayList<>();

  // 建立空的回傳資料盒。
  public QuizResponseDto() {
  }

  // 建立完整的回傳資料盒。
  public QuizResponseDto(
    Long id,
    String title,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isPublished,
    List<QuestionResponse> questions) {

    this.id = id;
    this.title = title;
    this.description = description;
    this.startDate = startDate;
    this.endDate = endDate;
    this.isPublished = isPublished;
    this.questions = questions;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public List<QuestionResponse> getQuestions() {
    return questions;
  }

  public void setQuestions(List<QuestionResponse> questions) {
    this.questions = questions;
  }
}
