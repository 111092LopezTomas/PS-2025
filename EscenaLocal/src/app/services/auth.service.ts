import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface Rol {
  id: number;
  rol: string;
}

export interface AuthRequest {
  username: string;
  password: string;
  email?: string;
  rol: string;
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
  private userIdKey = 'userId';

  // estado reactivo
  private loggedIn$ = new BehaviorSubject<boolean>(!!this.getToken());
  private avatarUrl$ = new BehaviorSubject<string | null>(null);

  // expuestos
  loginState$ = this.loggedIn$.asObservable();
  avatarState$ = this.avatarUrl$.asObservable();

  constructor(private http: HttpClient) {}

  // =====================
  // AUTH HTTP CALLS
  // =====================

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => {
        // 1) guardo token con la key correcta
        this.saveToken(response.token);
        console.log('LOGIN RESPONSE:', response);
        console.log('TOKEN GUARDADO:', localStorage.getItem('auth_token'));
        // 2) guardo userId
        localStorage.setItem(this.userIdKey, response.userId.toString());

        // 3) emito que hay login
        this.loggedIn$.next(true);

        // 4) emito la URL de la imagen para navbar/perfil
        // tu endpoint: GET /auth/{id}/imagen
        const imgUrl = `${this.apiUrl}/${response.userId}/imagen?ts=${Date.now()}`;
        this.setAvatar(imgUrl);
      })
    );
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

    // si tu backend devuelve también token y userId en el register
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, formData).pipe(
      tap((response) => {
        if (response?.token) {
          this.saveToken(response.token);
          localStorage.setItem(this.userIdKey, response.userId.toString());
          this.loggedIn$.next(true);

          const imgUrl = `${this.apiUrl}/${response.userId}/imagen?ts=${Date.now()}`;
          this.setAvatar(imgUrl);
        }
      })
    );
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

  getUserId(): number | null {
    const raw = localStorage.getItem(this.userIdKey);
    return raw ? Number(raw) : null;
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

  getRoleFromToken(): string | null {
    const token = this.getToken();
    if (!token) return null;

    const payload = this.decodeToken(token);
    if (!payload) return null;

    const role =
      payload.role ||
      payload.rol ||
      (Array.isArray(payload.authorities) ? payload.authorities[0] : null) ||
      (Array.isArray(payload.roles) ? payload.roles[0] : null);

    return role ?? null;
  }

  validateSelectedRole(selectedRole: string): boolean {
    const token = this.getToken();
    if (!token) {
      console.log('No hay token');
      return false;
    }
    const payload = this.decodeToken(token);
    console.log('payload del token:', payload);

    const realRole =
      payload.role ||
      payload.rol ||
      (Array.isArray(payload.authorities) ? payload.authorities[0] : null);

    console.log('rol real:', realRole, 'rol elegido:', selectedRole);

    return realRole === selectedRole;
  }

  // =====================
  // LOGIN STATE HELPERS
  // =====================

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    this.clearToken();
    localStorage.removeItem(this.userIdKey);
    this.loggedIn$.next(false);
    this.avatarUrl$.next(null);
    this.clearProductorId();
  }

  // =====================
  // AVATAR
  // =====================

  setAvatar(url: string | null): void {
    this.avatarUrl$.next(url);
  }

  registerArtProd(body: any): Observable<AuthResponse> {
  return this.http.post<AuthResponse>('http://localhost:8080/auth/register/art-prod', body);
  }

  getUserIdFromToken(): number | null {
    const token = this.getToken();
    if (!token) return null;

    const decoded = this.decodeToken(token);
    if (!decoded) return null;

    return decoded.id || decoded.userId || null;
  }

  getProductorIdFromToken(): number | null {
  const token = this.getToken();
  if (!token) return null;

  const decoded = this.decodeToken(token);
  if (!decoded) return null;

  // probables nombres según cómo lo armes en el backend
  return (
    decoded.productorId ??
    decoded.idProductor ??
    decoded.productor_id ??
    null
  );
}


   getUserRoleFromToken(): string | null {
    const token = this.getToken();
    if (!token) return null;

    const decoded = this.decodeToken(token);
    if (!decoded) return null;

    // en tu back dijiste que generabas algo como jwtUtil.generateToken(u.getUsername(), u.getRol().getRol());
    // así que probablemente venga como "role" o "rol"
    return decoded.role || decoded.rol || null;
  }

  // 👉 este es el que te faltaba
  tieneRol(rol: string): boolean {
    const userRol = this.getUserRoleFromToken();
    return userRol === rol;
  }

  requestPasswordReset(email: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/forgot-password`,
      { email: email }
    );
  }

  resetPassword(token: string, newPassword: string) {
    console.log('Frontend envía token:', token);
  return this.http.post(`${this.apiUrl}/reset-password`,{ token, newPassword }
  );
  }

  cambiarPassword(data: { actual: string; nueva: string }) {
  return this.http.put(`${this.apiUrl}/change-password`, data);
}

private productorIdKey = 'productorId';

setProductorId(id: number): void {
  localStorage.setItem(this.productorIdKey, String(id));
}

getProductorId(): number | null {
  const raw = localStorage.getItem(this.productorIdKey);
  return raw ? Number(raw) : null;
}

clearProductorId(): void {
  localStorage.removeItem(this.productorIdKey);
}

setSession(token: string, userId: number): void {
  // token + userId con las KEYS correctas del servicio
  this.saveToken(token);
  localStorage.setItem(this.userIdKey, String(userId));

  // emito estado logueado
  this.loggedIn$.next(true);

  // emito avatar (para navbar/perfil)
  const imgUrl = `${this.apiUrl}/${userId}/imagen?ts=${Date.now()}`;
  this.setAvatar(imgUrl);
}

}




