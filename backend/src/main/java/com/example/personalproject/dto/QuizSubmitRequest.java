package com.example.personalproject.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class QuizSubmitRequest {

  @NotEmpty(message = "至少要回答一題")
  private List<@Valid AnswerRequest> answers;

  public List<AnswerRequest> getAnswers() {
    return answers;
  }

  public void setAnswers(List<AnswerRequest> answers) {
    this.answers = answers;
  }
}
