package com.example.personalproject.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 一整份問卷的統計結果。
 *
 * 這個 DTO 是統計 API 最外層的回傳資料盒。
 */
public class QuizStatResponse {

  // 問卷 ID。
  private Long quizId;

  // 問卷標題。
  private String quizTitle;

  // 這份問卷總共有幾位填答者。
  private long totalRespondents;

  // 每一題的統計資料。
  private List<QuestionStatDto> questionStats = new ArrayList<>();

  // Jackson 建立回傳物件時需要無參數建構子。
  public QuizStatResponse() {
  }

  public QuizStatResponse(
    Long quizId,
    String quizTitle,
    long totalRespondents,
    List<QuestionStatDto> questionStats) {

    this.quizId = quizId;
    this.quizTitle = quizTitle;
    this.totalRespondents = totalRespondents;
    this.questionStats = questionStats;
  }

  public Long getQuizId() {
    return quizId;
  }

  public void setQuizId(Long quizId) {
    this.quizId = quizId;
  }

  public String getQuizTitle() {
    return quizTitle;
  }

  public void setQuizTitle(String quizTitle) {
    this.quizTitle = quizTitle;
  }

  public long getTotalRespondents() {
    return totalRespondents;
  }

  public void setTotalRespondents(long totalRespondents) {
    this.totalRespondents = totalRespondents;
  }

  public List<QuestionStatDto> getQuestionStats() {
    return questionStats;
  }

  public void setQuestionStats(List<QuestionStatDto> questionStats) {
    this.questionStats = questionStats;
  }
}
