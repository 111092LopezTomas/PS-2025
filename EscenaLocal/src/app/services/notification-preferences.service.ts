import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Observable } from 'rxjs';

export interface NotificationPreferences {
  email: boolean;
  push: boolean;

  marketingNovedades: boolean;
  recordatoriosEventos: boolean;
  mensajesDirectos: boolean;

  invitacionesAEventos: boolean;
  nuevosSeguidores: boolean;

  ventasEntradas: boolean;
  pagosMercadopago: boolean;

  alertasCapacidad: boolean;
}

@Injectable({ providedIn: 'root' })
export class NotificationPreferencesService {
  private base = `${environment.apiBaseLocal}/notification-preferences`;

  constructor(private http: HttpClient) {}

  getMine(): Observable<NotificationPreferences> {
    return this.http.get<NotificationPreferences>(`${this.base}/me`);
  }

  updateMine(prefs: NotificationPreferences): Observable<NotificationPreferences> {
    return this.http.put<NotificationPreferences>(`${this.base}/me`, prefs);
  }
}
