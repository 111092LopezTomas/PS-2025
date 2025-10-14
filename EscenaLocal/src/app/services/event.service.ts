import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EntradaDto {
  tipo: string;
  precio: number;
  disponibilidad: number;
}

export interface EventGet {
  id: number;
  activo: Boolean;
  descripcion: String;
  evento: String;
  fecha: Date;
  hora: Date;
  artistas: String[];
  clasificacion: String;
  establecimiento: String;
  barrio: String;
  ciudad: String;
  provincia: String;
  imagen: string;
  direccion: string;
  capacidad: number;
  genero: string;
  duracion: string;
  destacados: string[];
  entradasDetalle: EntradaDto[];
  productor: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getEvents(): Observable<EventGet[]> {
    return this.http.get<EventGet[]>(this.apiUrl+"/eventos/all");
  }

  getImagenEvento(id: number): Observable<Blob> {
  return this.http.get(`http://localhost:8080/eventos/${id}/imagen`, {
    responseType: 'blob'
  });
  }

  getArtistas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/artistas/all`);
  }

  getProductores(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/productores/all`);
  }

  getTiposEntrada(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/entradas/all`);
  }

  getEstablecimientos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/establecimientos/all`);
  }

  getClasificaciones(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clasificaciones/all`);
  }

  crearEvento(evento: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/eventos/nuevo`, evento);
  }

  getEventById(id: number): Observable<EventGet> {
    return this.http.get<EventGet>(`${this.apiUrl}/eventos/${id}`)
  }

  comprarEntrada(eventoId: number, cantidad: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/compras`, {
      eventoId,
      cantidad
    })
  }
}
