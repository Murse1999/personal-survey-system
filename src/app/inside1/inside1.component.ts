import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../core/auth.service';
import {
  QuestionResponse,
  QuizApiService,
  QuizResponseDto
} from '../core/quiz-api.service';

@Component({
  selector: 'app-inside1',
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './inside1.component.html',
  styleUrl: './inside1.component.scss'
})
export class Inside1Component {
  quiz?: QuizResponseDto;
  isLoading = true;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  selectedOptions: Record<number, number[]> = {};
  textAnswers: Record<number, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizApi: QuizApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const quizId = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(quizId)) {
      this.errorMessage = '問卷網址不正確';
      this.isLoading = false;
      return;
    }

    this.quizApi.getQuiz(quizId).subscribe({
      next: (quiz) => {
        this.quiz = quiz;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = '找不到這份問卷';
        this.isLoading = false;
      }
    });
  }

  isQuestionAnswered(question: QuestionResponse): boolean {
    if (question.type === 'TEXT') {
      return Boolean(this.textAnswers[question.id]?.trim());
    }

    return (this.selectedOptions[question.id] ?? []).length > 0;
  }

  isOpenForAnswers(): boolean {
    if (!this.quiz || this.quiz.isOwner || !this.quiz.isPublished) {
      return false;
    }

    const now = new Date();
    return now >= new Date(this.quiz.startDate)
      && now <= new Date(this.quiz.endDate);
  }

  isOptionSelected(questionId: number, optionId: number): boolean {
    return (this.selectedOptions[questionId] ?? []).includes(optionId);
  }

  selectSingle(questionId: number, optionId: number): void {
    this.selectedOptions[questionId] = [optionId];
    this.clearAnswerError();
  }

  toggleOption(questionId: number, optionId: number): void {
    const selected = this.selectedOptions[questionId] ?? [];
    this.selectedOptions[questionId] = selected.includes(optionId)
      ? selected.filter((id) => id !== optionId)
      : [...selected, optionId];
    this.clearAnswerError();
  }

  clearAnswerError(): void {
    this.errorMessage = '';
  }

  submit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.quiz) {
      return;
    }

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }

    const unansweredRequired = this.quiz.questions.find(
      (question) => question.isRequired && !this.isQuestionAnswered(question)
    );

    if (unansweredRequired) {
      this.errorMessage = `第 ${unansweredRequired.questionNum} 題尚未完成`;
      return;
    }

    const answers = this.quiz.questions.map((question) => ({
      questionId: question.id,
      optionIds: this.selectedOptions[question.id] ?? [],
      answerText: this.textAnswers[question.id] ?? ''
    }));

    this.isSubmitting = true;

    this.quizApi.submitQuiz(this.quiz.id, {
      answers
    }).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigateByUrl('/');
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.errorMessage = error.error || '提交失敗，請稍後再試';
      }
    });
  }

}
