import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductorDashboardDto } from '../models/dashboard.model';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = environment.apiBaseLocal; // ej: 'http://localhost:8080/api'

  constructor(private http: HttpClient) {}

getDashboardProductor(from: string, to: string, productorId: number): Observable<ProductorDashboardDto> {
  const params = new HttpParams()
    .set('from', from)
    .set('to', to)
    .set('productorId', productorId);

  return this.http.get<ProductorDashboardDto>(`${this.apiUrl}/dashboard/productor`, { params });
}

getDashboardArtista(from: string, to: string, artistaId: number) {
  return this.http.get<any>(
    `${this.apiUrl}/dashboard/artista?from=${from}&to=${to}&artistaId=${artistaId}`
  );
}

}
