import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../core/auth.service';
import {
  OptionRequest,
  QuestionType,
  QuizApiService,
  QuizRequest
} from '../core/quiz-api.service';

interface QuestionDraft {
  id: number;
  questionNum: number;
  title: string;
  type: QuestionType;
  isRequired: boolean;
  options: OptionRequest[];
}

@Component({
  selector: 'app-create-quiz',
  imports: [FormsModule, RouterLink],
  templateUrl: './create-quiz.component.html',
  styleUrl: './create-quiz.component.scss'
})
export class CreateQuizComponent {
  title = '';
  description = '';
  startDate = '';
  endDate = '';
  isPublished = false;
  questions: QuestionDraft[] = [this.createQuestion(1)];
  isSubmitting = false;
  isLoadingQuiz = false;
  isPreviewMode = false;
  isEditMode = false;
  errorMessage = '';
  quizId?: number;

  private nextQuestionId = 2;

  constructor(
    private quizApi: QuizApiService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (!idParam) {
      return;
    }

    const quizId = Number(idParam);
    if (!Number.isInteger(quizId)) {
      this.errorMessage = '問卷網址不正確';
      return;
    }

    this.quizId = quizId;
    this.isEditMode = true;
    this.isLoadingQuiz = true;

    this.quizApi.getQuiz(quizId).subscribe({
      next: (quiz) => {
        this.title = quiz.title;
        this.description = quiz.description ?? '';
        this.startDate = quiz.startDate.slice(0, 10);
        this.endDate = quiz.endDate.slice(0, 10);
        this.isPublished = quiz.isPublished;
        this.nextQuestionId = 1;
        this.questions = quiz.questions.map((question) => ({
          id: this.nextQuestionId++,
          questionNum: question.questionNum,
          title: question.title,
          type: question.type,
          isRequired: question.isRequired,
          options: question.options.map((option) => ({
            optionCode: option.optionCode,
            optionText: option.optionText
          }))
        }));
        this.isLoadingQuiz = false;
      },
      error: (error: HttpErrorResponse) => {
        this.isLoadingQuiz = false;
        this.errorMessage = error.status === 403
          ? '你沒有權限編輯這份問卷'
          : '找不到這份問卷';
      }
    });
  }

  togglePreview(): void {
    this.isPreviewMode = !this.isPreviewMode;
  }

  addQuestion(): void {
    this.questions.push(this.createQuestion(this.questions.length + 1));
  }

  removeQuestion(index: number): void {
    if (this.questions.length === 1) {
      return;
    }

    this.questions.splice(index, 1);
    this.renumberQuestions();
  }

  addOption(question: QuestionDraft): void {
    question.options.push({
      optionCode: this.nextOptionCode(question.options.length),
      optionText: ''
    });
  }

  removeOption(question: QuestionDraft, index: number): void {
    question.options.splice(index, 1);
  }

  submit(): void {
    this.errorMessage = '';

    if (!this.authService.isLoggedIn()) {
      this.router.navigateByUrl('/login');
      return;
    }

    if (!this.title.trim() || !this.startDate || !this.endDate) {
      this.errorMessage = '請完成問卷標題和日期';
      return;
    }

    if (this.endDate < this.startDate) {
      this.errorMessage = '結束日期不能早於開始日期';
      return;
    }

    const invalidQuestion = this.questions.find((question) =>
      !question.title.trim()
      || (question.type !== 'TEXT'
        && question.options.some((option) => !option.optionText.trim()))
    );

    if (invalidQuestion) {
      this.errorMessage = '請完成每一題的題目內容和選項文字';
      return;
    }

    const request: QuizRequest = {
      title: this.title.trim(),
      description: this.description.trim(),
      startDate: `${this.startDate}T00:00:00`,
      endDate: `${this.endDate}T23:59:59`,
      isPublished: this.isPublished,
      questions: this.questions.map((question) => ({
        questionNum: question.questionNum,
        title: question.title.trim(),
        type: question.type,
        isRequired: question.isRequired,
        options: question.type === 'TEXT' ? [] : question.options
      }))
    };

    this.isSubmitting = true;

    if (this.isEditMode && this.quizId) {
      this.quizApi.updateQuiz(this.quizId, request).subscribe({
        next: () => {
          this.finishSave(this.quizId!);
        },
        error: (error: HttpErrorResponse) => {
          this.handleSaveError(error);
        }
      });
      return;
    }

    this.quizApi.createQuiz(request).subscribe({
      next: (quizId) => {
        this.finishSave(quizId);
      },
      error: (error: HttpErrorResponse) => {
        this.handleSaveError(error);
      }
    });
  }

  private finishSave(quizId: number): void {
    this.isSubmitting = false;
    this.router.navigate(['/quiz', quizId]);
  }

  private handleSaveError(error: HttpErrorResponse): void {
    this.isSubmitting = false;
    this.errorMessage = error.error || (this.isEditMode
      ? '儲存問卷失敗，請稍後再試'
      : '建立問卷失敗，請稍後再試');
  }

  private createQuestion(questionNum: number): QuestionDraft {
    return {
      id: this.nextQuestionId++,
      questionNum,
      title: '',
      type: 'SINGLE',
      isRequired: true,
      options: [
        { optionCode: 'A', optionText: '' },
        { optionCode: 'B', optionText: '' }
      ]
    };
  }

  private nextOptionCode(index: number): string {
    return String.fromCharCode('A'.charCodeAt(0) + index);
  }

  private renumberQuestions(): void {
    this.questions.forEach((question, index) => {
      question.questionNum = index + 1;
    });
  }
}
