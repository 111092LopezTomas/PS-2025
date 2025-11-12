import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EstablecimientoDetalle {
  id: number;
  establecimiento: string;
  capacidad: number;
  direccion: string;
  barrio: string;
  ciudad: string;
  provincia: string;
  
}

@Injectable({
  providedIn: 'root',
})
export class EstablishmentService {
  private apiBase = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getEstablecimientoById(id: number | string): Observable<EstablecimientoDetalle> {
    return this.http.get<EstablecimientoDetalle>(
      `${this.apiBase}/establecimientos/${id}`
    );
  }
}
