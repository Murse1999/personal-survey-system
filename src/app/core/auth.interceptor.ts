import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from './auth.service';
import { API_URL } from './api.config';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  // 只有呼叫自己的後端，而且已經登入時，才加入 JWT。
  if (!token || !request.url.startsWith(API_URL)) {
    return next(request);
  }

  return next(
    request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
  ).pipe(
    catchError((error) => {
      // 這兩個 API 是登入後的基本資料請求。若它們回傳 401/403，
      // 代表瀏覽器保存的 JWT 已失效，清掉舊狀態讓使用者重新登入。
      const isSessionRequest = request.url.endsWith('/users/me')
        || request.url.endsWith('/quiz/mine');

      if (isSessionRequest && (error.status === 401 || error.status === 403)) {
        authService.logout();
        void router.navigateByUrl('/login');
      }

      return throwError(() => error);
    })
  );
};
