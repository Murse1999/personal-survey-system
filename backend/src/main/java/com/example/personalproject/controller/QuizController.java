package com.example.personalproject.controller;

import com.example.personalproject.dto.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.example.personalproject.service.QuizService;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

  private final QuizService quizService;

  public QuizController(QuizService quizService) {
    this.quizService = quizService;
  }

  @PostMapping
  public Long createQuiz(
    @RequestBody @Valid QuizRequest quizRequest,
    Authentication authentication) {

    return quizService.addQuiz(quizRequest, authentication.getName());
  }

  @GetMapping("/{id}")
  public QuizResponseDto getQuiz(
    @PathVariable Long id,
    Authentication authentication) {

    String currentUserEmail = authentication == null
      ? null
      : authentication.getName();
    boolean isAdmin = authentication != null
      && authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    return quizService.seeQuizForViewer(id, currentUserEmail, isAdmin);
  }

  @GetMapping("/mine")
  public List<QuizResponseDto> getMyQuizzes(Authentication authentication) {
    return quizService.seeMyQuizzes(authentication.getName());
  }

  @GetMapping
  public List<QuizResponseDto> getQuizzes() {
    return quizService.seeAllQuizzes();
  }

  @PutMapping("/{id}")
  public QuizResponseDto updateQuiz(
    @RequestBody @Valid QuizRequest quizRequest,
    @PathVariable Long id,
    Authentication authentication) {

    boolean isAdmin = authentication.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    return quizService.updateQuiz(
      quizRequest,
      id,
      authentication.getName(),
      isAdmin
    );
  }

  @DeleteMapping("/{id}")
  public String deleteQuiz(
    @PathVariable Long id,
    Authentication authentication) {

    boolean isAdmin = authentication.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    return quizService.deleteQuiz(id, authentication.getName(), isAdmin);
  }

  @DeleteMapping
  public String deleteAllQuizzes(
    @RequestBody @Valid QuizDeleteRequest quizDeleteRequest,
    Authentication authentication) {

    boolean isAdmin = authentication.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    return quizService.deleteQuizzes(
      quizDeleteRequest,
      authentication.getName(),
      isAdmin
    );
  }

  @PostMapping("/{quizId}/submit")
  public Long submitQuiz(
    @PathVariable Long quizId,
    @RequestBody @Valid QuizSubmitRequest request,
    Authentication authentication) {

    return quizService.submitQuiz(
      quizId,
      request,
      authentication.getName()
    );
  }

  @GetMapping("/{quizId}/responses")
  public List<QuizSubmissionResponse> getResponses(@PathVariable Long quizId) {
    return quizService.searchByQuizId(quizId);
  }

  @GetMapping("/{responseId}/details")
  public List<ResponseDetailResponse> getResponseDetail(@PathVariable Long responseId) {
    return quizService.searchByResponseId(responseId);
  }

  @GetMapping("/{id}/statistics")
  public QuizStatResponse quizStatistics(
    @PathVariable Long id,
    Authentication authentication) {

    boolean isAdmin = authentication.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

    return quizService.quizStat(id, authentication.getName(), isAdmin);
  }
}
