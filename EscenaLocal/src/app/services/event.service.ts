import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Event {
  id: Number;
  activo: Boolean;
  descripcion: String;
  evento: String;
  fecha: Date;
  hora: Date;
  artistas: String[];
  entradas: String[];
  clasificacion: String;
  establecimiento: String;
  ciudad: String;
  provincia: String;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8080/eventos/all';

  constructor(private http: HttpClient) {}

  getEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl);
  }
}
