package com.example.personalproject.dto;

import com.example.personalproject.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

/**
 * 一整題的統計資料。
 *
 * 選擇題使用 optionStats，
 * 文字題使用 textAnswers。
 */
public class QuestionStatDto {

  // 題目 ID。
  private Long questionId;

  // 題目在問卷中的順序。
  private Integer questionNum;

  // 題目文字。
  private String questionTitle;

  // 題型，例如 SINGLE、MULTI、TEXT。
  private QuestionType questionType;

  // 選擇題的選項統計資料。
  private List<OptionStatDto> optionStats = new ArrayList<>();

  // 文字題的所有回答內容。
  private List<String> textAnswers = new ArrayList<>();

  // Jackson 建立回傳物件時需要無參數建構子。
  public QuestionStatDto() {
  }

  public QuestionStatDto(
    Long questionId,
    Integer questionNum,
    String questionTitle,
    QuestionType questionType,
    List<OptionStatDto> optionStats,
    List<String> textAnswers) {

    this.questionId = questionId;
    this.questionNum = questionNum;
    this.questionTitle = questionTitle;
    this.questionType = questionType;
    this.optionStats = optionStats;
    this.textAnswers = textAnswers;
  }

  public Long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public Integer getQuestionNum() {
    return questionNum;
  }

  public void setQuestionNum(Integer questionNum) {
    this.questionNum = questionNum;
  }

  public String getQuestionTitle() {
    return questionTitle;
  }

  public void setQuestionTitle(String questionTitle) {
    this.questionTitle = questionTitle;
  }

  public QuestionType getQuestionType() {
    return questionType;
  }

  public void setQuestionType(QuestionType questionType) {
    this.questionType = questionType;
  }

  public List<OptionStatDto> getOptionStats() {
    return optionStats;
  }

  public void setOptionStats(List<OptionStatDto> optionStats) {
    this.optionStats = optionStats;
  }

  public List<String> getTextAnswers() {
    return textAnswers;
  }

  public void setTextAnswers(List<String> textAnswers) {
    this.textAnswers = textAnswers;
  }
}
