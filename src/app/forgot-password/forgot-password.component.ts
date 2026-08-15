import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import {
  AuthService,
  PasswordResetConfirmRequest
} from '../core/auth.service';

@Component({
  selector: 'app-forgot-password',
  imports: [FormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
  email = '';
  code = '';
  newPassword = '';
  confirmPassword = '';
  step: 'request' | 'confirm' = 'request';
  errorMessage = '';
  noticeMessage = '';
  isSubmitting = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  requestCode(): void {
    this.errorMessage = '';
    this.noticeMessage = '';

    if (!this.isValidEmail(this.email)) {
      this.errorMessage = '請輸入正確的 Email';
      return;
    }

    this.isSubmitting = true;
    this.authService.requestPasswordReset(this.email.trim()).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.step = 'confirm';
        this.noticeMessage = response.message;
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.errorMessage = error.error || '驗證碼寄送失敗，請稍後再試';
      }
    });
  }

  confirmReset(): void {
    this.errorMessage = '';
    this.noticeMessage = '';

    if (!/^\d{6}$/.test(this.code.trim())) {
      this.errorMessage = '請輸入 6 位數字驗證碼';
      return;
    }

    if (this.newPassword.length < 8) {
      this.errorMessage = '新密碼至少需要 8 個字元';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = '兩次輸入的新密碼不一致';
      return;
    }

    const request: PasswordResetConfirmRequest = {
      email: this.email.trim(),
      code: this.code.trim(),
      newPassword: this.newPassword
    };

    this.isSubmitting = true;
    this.authService.confirmPasswordReset(request).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.router.navigate(['/login'], {
          queryParams: { reset: '1' }
        });
        this.noticeMessage = response.message;
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.errorMessage = error.error || '密碼更新失敗，請重新取得驗證碼';
      }
    });
  }

  requestAnotherCode(): void {
    this.step = 'request';
    this.code = '';
    this.newPassword = '';
    this.confirmPassword = '';
    this.errorMessage = '';
    this.noticeMessage = '';
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());
  }
}
