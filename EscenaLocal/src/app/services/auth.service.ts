import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AuthRequest {
  username: string;
  password: string;
  email?: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  imagen?: File; // 👈 se agrega el campo para la imagen
}

export interface AuthResponse {
  token: string;
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    // ✅ Convertimos el request en FormData
    const formData = new FormData();
    formData.append('username', data.username);
    formData.append('password', data.password);
    formData.append('email', data.email);

    // Si el usuario eligió una imagen, la agregamos
    if (data.imagen) {
      formData.append('imagen', data.imagen);
    }

    // ✅ No agregues headers manualmente, Angular lo hace solo
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, formData);
  }
}

