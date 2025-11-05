import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Rol {
  id: number;
  rol: string;
}

export interface AuthRequest {
  username: string;
  password: string;
  email?: string;
  rol: string; // el que eligió en el combo
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  imagen?: File;
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
  private tokenKey = 'auth_token';

  constructor(private http: HttpClient) {}

  // =====================
  // AUTH HTTP CALLS
  // =====================

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
  }

  register(data: RegisterRequest, rolId: number): Observable<AuthResponse> {
    const formData = new FormData();
    formData.append('username', data.username);
    formData.append('password', data.password);
    formData.append('email', data.email);
    formData.append('rolId', rolId.toString());

    if (data.imagen) {
      formData.append('imagen', data.imagen);
    }

    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, formData);
  }

  getRoles(): Observable<Rol[]> {
    return this.http.get<Rol[]>(`${this.apiUrl}/roles`);
  }

  // =====================
  // TOKEN STORAGE
  // =====================

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  // =====================
  // JWT UTILS
  // =====================

  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload);
      return JSON.parse(decoded);
    } catch (e) {
      return null;
    }
  }

  /**
   * Devuelve el rol real que viene en el token.
   * Ajustá acá según cómo lo estés mandando desde Spring.
   */
  getRoleFromToken(): string | null {
    const token = this.getToken();
    if (!token) return null;

    const payload = this.decodeToken(token);
    if (!payload) return null;

    // acá probamos varios nombres típicos
    const role =
      payload.role ||
      payload.rol ||
      (Array.isArray(payload.authorities) ? payload.authorities[0] : null) ||
      (Array.isArray(payload.roles) ? payload.roles[0] : null);

    return role ?? null;
  }

  /**
   * Compara el rol que eligió en el combo con el que realmente tiene el usuario.
   * Devuelve true si coincide, false si no.
   */
  /* validateSelectedRole(selectedRole: string): boolean {
    const realRole = this.getRoleFromToken();
    if (!realRole) return false;
    return realRole === selectedRole; */
  // }

  validateSelectedRole(selectedRole: string): boolean {
  const token = this.getToken();
  if (!token) {
    console.log('No hay token');
    return false;
  }
  const payload = this.decodeToken(token);
  console.log('payload del token:', payload); // 👈 mirá acá qué viene

  const realRole =
    payload.role ||
    payload.rol ||
    (Array.isArray(payload.authorities) ? payload.authorities[0] : null);

  console.log('rol real:', realRole, 'rol elegido:', selectedRole);

  return realRole === selectedRole;
}


  /**
   * Útil para guards
   */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
