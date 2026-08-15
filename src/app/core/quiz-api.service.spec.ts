import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { API_URL } from './api.config';
import { QuizApiService } from './quiz-api.service';

describe('QuizApiService', () => {
  let service: QuizApiService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        QuizApiService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(QuizApiService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('reads the delete response as plain text', () => {
    let result = '';

    service.deleteQuiz(17).subscribe((response) => {
      result = response;
    });

    const request = httpTesting.expectOne(`${API_URL}/quiz/17`);

    expect(request.request.method).toBe('DELETE');
    expect(request.request.responseType).toBe('text');

    request.flush('success');

    expect(result).toBe('success');
  });
});
