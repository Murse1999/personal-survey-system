import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

import { API_URL } from './api.config';

export type AvatarType = 'MALE' | 'FEMALE';

export interface LoginResponse {
  token: string;
  email: string;
  role: string;
  name: string;
  phone: string;
  age: number | null;
  avatarType: AvatarType;
}

export interface CurrentUser {
  email: string;
  role: string;
  name: string;
  phone: string;
  age: number | null;
  avatarType: AvatarType;
}

export interface UserProfileResponse extends CurrentUser {
  id: number;
}

export interface UserProfileUpdateRequest {
  name: string;
  phone: string;
  age: number | null;
  avatarType: AvatarType;
}

export interface RegisterRequest {
  name: string;
  phone: string;
  password: string;
  email: string;
  age: number | null;
  avatarType: AvatarType;
}

export interface PasswordResetResponse {
  message: string;
}

export interface PasswordResetConfirmRequest {
  email: string;
  code: string;
  newPassword: string;
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

  requestPasswordReset(email: string): Observable<PasswordResetResponse> {
    return this.http.post<PasswordResetResponse>(
      `${API_URL}/users/password-reset/request`,
      { email }
    );
  }

  confirmPasswordReset(
    request: PasswordResetConfirmRequest
  ): Observable<PasswordResetResponse> {
    return this.http.post<PasswordResetResponse>(
      `${API_URL}/users/password-reset/confirm`,
      request
    );
  }

  getMyProfile(): Observable<UserProfileResponse> {
    return this.http
      .get<UserProfileResponse>(`${API_URL}/users/me`)
      .pipe(tap((profile) => this.saveProfile(profile)));
  }

  updateMyProfile(request: UserProfileUpdateRequest): Observable<UserProfileResponse> {
    return this.http
      .put<UserProfileResponse>(`${API_URL}/users/me`, request)
      .pipe(tap((profile) => this.saveProfile(profile)));
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
    this.saveCurrentUser({
      email: response.email,
      role: response.role,
      name: response.name,
      phone: response.phone,
      age: response.age,
      avatarType: response.avatarType
    });
  }

  private saveProfile(profile: UserProfileResponse): void {
    this.saveCurrentUser({
      email: profile.email,
      role: profile.role,
      name: profile.name,
      phone: profile.phone,
      age: profile.age,
      avatarType: profile.avatarType
    });
  }

  private saveCurrentUser(user: CurrentUser): void {
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  private readStoredUser(): CurrentUser | null {
    const storedUser = localStorage.getItem(this.userKey);

    if (!storedUser) {
      return null;
    }

    try {
      const parsed = JSON.parse(storedUser) as Partial<CurrentUser>;
      return {
        email: parsed.email ?? '',
        role: parsed.role ?? 'USER',
        name: parsed.name ?? '',
        phone: parsed.phone ?? '',
        age: parsed.age ?? null,
        avatarType: parsed.avatarType === 'FEMALE' ? 'FEMALE' : 'MALE'
      };
    } catch {
      localStorage.removeItem(this.userKey);
      return null;
    }
  }
}
