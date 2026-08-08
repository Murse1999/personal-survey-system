import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService, RegisterRequest } from '../core/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  name = '';
  phone = '';
  email = '';
  password = '';
  confirmPassword = '';
  age: number | null = null;
  errorMessage = '';
  isSubmitting = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  submit(): void {
    this.errorMessage = '';

    if (!this.name.trim() || !this.phone.trim() || !this.email.trim()) {
      this.errorMessage = '請完成姓名、電話和 Email';
      return;
    }

    if (this.password.length < 8) {
      this.errorMessage = '密碼至少需要 8 個字元';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.errorMessage = '兩次輸入的密碼不一致';
      return;
    }

    const request: RegisterRequest = {
      name: this.name.trim(),
      phone: this.phone.trim(),
      email: this.email.trim(),
      password: this.password,
      age: this.age
    };

    this.isSubmitting = true;

    this.authService.register(request).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/login'], { queryParams: { registered: '1' } });
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.errorMessage = error.error || '註冊失敗，請稍後再試';
      }
    });
  }
}
