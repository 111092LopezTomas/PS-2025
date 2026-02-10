// auth.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './services/auth.service';


@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const token = this.authService.getToken(); // de localStorage, etc.
    console.log('➡️ Request a:', req.url, ' token:', token);
    // NO agregamos token a login/register, pero sí al resto
    if (token &&
        !req.url.includes('/auth/login') &&
        !req.url.includes('/auth/register')) {

      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });

      return next.handle(authReq);
    }
    console.log("Interceptor ejecutado. Token:", token);
    return next.handle(req);
  }
}
