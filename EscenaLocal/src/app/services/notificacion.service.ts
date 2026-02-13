import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Notificacion {
  id: number;
  mensaje: string;
  leido: boolean;
  creado?: string;
}

export interface NotificationBadge {
  unreadCount: number;
}

@Injectable({ providedIn: 'root' })
export class NotificacionService {
  // AJUSTÁ según tu backend real
  private baseUrl = 'http://localhost:8080/api/notificaciones';

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt') || localStorage.getItem('token');
    return new HttpHeaders({
      Authorization: token ? `Bearer ${token}` : ''
    });
  }

  obtenerNotificaciones(userId: number): Observable<Notificacion[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Notificacion[]>(`${this.baseUrl}/${userId}`, { headers });
  }

  marcarComoLeido(id: number): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.patch(`${this.baseUrl}/${id}/leido`, {}, { headers, responseType: 'text' as 'json' });
  }

  getBadge(): Observable<NotificationBadge> {
    return this.http.get<NotificationBadge>(`${this.baseUrl}/me/badge`);
  }

  getMyNotifications(page = 0, size = 10): Observable<any> {
    // backend devuelve Page<NotificacionItemDto>
    return this.http.get<any>(`${this.baseUrl}/me`, { params: { page, size } as any });
  }

  markAsRead(id: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/${id}/read`, {});
  }

  markAllAsRead(): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/me/read-all`, {});
  }
}
