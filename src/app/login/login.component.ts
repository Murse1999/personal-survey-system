import { HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';
  noticeMessage = '';
  isSubmitting = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    if (this.route.snapshot.queryParamMap.get('registered') === '1') {
      this.noticeMessage = '註冊成功，請使用新帳號登入';
    }
  }

  submitLogin(): void {
    this.errorMessage = '';

    if (!this.email.trim() || !this.password) {
      this.errorMessage = '請輸入 Email 和密碼';
      return;
    }

    this.isSubmitting = true;

    this.authService.login(this.email.trim(), this.password).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigateByUrl('/');
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;

        if (error.status === 401 || error.status === 400) {
          this.errorMessage = 'Email 或密碼錯誤';
        } else if (error.status === 0) {
          this.errorMessage = '目前無法連線到後端，請確認後端是否啟動';
        } else {
          this.errorMessage = '登入失敗，請稍後再試';
        }
      }
    });
  }
}
