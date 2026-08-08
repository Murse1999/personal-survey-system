import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_URL } from './api.config';

export type QuestionType = 'SINGLE' | 'MULTI' | 'TEXT';

export interface OptionResponse {
  id: number;
  optionCode: string;
  optionText: string;
}

export interface QuestionResponse {
  id: number;
  questionNum: number;
  title: string;
  type: QuestionType;
  isRequired: boolean;
  options: OptionResponse[];
}

export interface QuizResponseDto {
  id: number;
  title: string;
  description: string | null;
  startDate: string;
  endDate: string;
  isPublished: boolean;
  questions: QuestionResponse[];
}

export interface OptionRequest {
  optionCode: string;
  optionText: string;
}

export interface QuestionRequest {
  questionNum: number;
  title: string;
  type: QuestionType;
  isRequired: boolean;
  options: OptionRequest[];
}

export interface QuizRequest {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  isPublished: boolean;
  questions: QuestionRequest[];
}

export interface AnswerRequest {
  questionId: number;
  optionIds?: number[];
  answerText?: string;
}

export interface QuizSubmitRequest {
  answers: AnswerRequest[];
}

export interface OptionStat {
  optionId: number;
  optionCode: string;
  optionText: string;
  selectedCount: number;
  percentage: number;
}

export interface QuestionStat {
  questionId: number;
  questionNum: number;
  questionTitle: string;
  questionType: QuestionType;
  optionStats: OptionStat[] | null;
  textAnswers: string[] | null;
}

export interface QuizStatResponse {
  quizId: number;
  quizTitle: string;
  totalRespondents: number;
  questionStats: QuestionStat[];
}

@Injectable({ providedIn: 'root' })
export class QuizApiService {
  constructor(private http: HttpClient) {}

  getQuizzes(): Observable<QuizResponseDto[]> {
    return this.http.get<QuizResponseDto[]>(`${API_URL}/quiz`);
  }

  getMyQuizzes(): Observable<QuizResponseDto[]> {
    return this.http.get<QuizResponseDto[]>(`${API_URL}/quiz/mine`);
  }

  getQuiz(id: number): Observable<QuizResponseDto> {
    return this.http.get<QuizResponseDto>(`${API_URL}/quiz/${id}`);
  }

  createQuiz(request: QuizRequest): Observable<number> {
    return this.http.post<number>(`${API_URL}/quiz`, request);
  }

  updateQuiz(id: number, request: QuizRequest): Observable<QuizResponseDto> {
    return this.http.put<QuizResponseDto>(`${API_URL}/quiz/${id}`, request);
  }

  deleteQuiz(id: number): Observable<string> {
    return this.http.delete<string>(`${API_URL}/quiz/${id}`);
  }

  submitQuiz(id: number, request: QuizSubmitRequest): Observable<number> {
    return this.http.post<number>(`${API_URL}/quiz/${id}/submit`, request);
  }

  getStatistics(id: number): Observable<QuizStatResponse> {
    return this.http.get<QuizStatResponse>(`${API_URL}/quiz/${id}/statistics`);
  }
}
