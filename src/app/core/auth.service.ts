import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

import { API_URL } from './api.config';

export interface LoginResponse {
  token: string;
  email: string;
  role: string;
}

export interface CurrentUser {
  email: string;
  role: string;
}

export interface RegisterRequest {
  name: string;
  phone: string;
  password: string;
  email: string;
  age: number | null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'personal-survey-system-jwt';
  private readonly userKey = 'personal-survey-system-user';

  private readonly currentUserSubject =
    new BehaviorSubject<CurrentUser | null>(this.readStoredUser());

  readonly currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${API_URL}/users/login`, { email, password })
      .pipe(tap((response) => this.saveSession(response)));
  }

  register(request: RegisterRequest): Observable<number> {
    return this.http.post<number>(`${API_URL}/users`, request);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getCurrentUser(): CurrentUser | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  private saveSession(response: LoginResponse): void {
    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(
      this.userKey,
      JSON.stringify({ email: response.email, role: response.role })
    );
    this.currentUserSubject.next({
      email: response.email,
      role: response.role
    });
  }

  private readStoredUser(): CurrentUser | null {
    const storedUser = localStorage.getItem(this.userKey);

    if (!storedUser) {
      return null;
    }

    try {
      return JSON.parse(storedUser) as CurrentUser;
    } catch {
      localStorage.removeItem(this.userKey);
      return null;
    }
  }
}
