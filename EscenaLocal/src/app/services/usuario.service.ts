import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })

export class UsuarioService {
  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) {}

  obtenerImagen(usuarioId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${usuarioId}/imagen`, { responseType: 'blob' });
  }

  subirImagen(usuarioId: number, file: File): Observable<void> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<void>(`${this.apiUrl}/${usuarioId}/imagen`, formData);
  }

  getUsuarioById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/usuarios/${id}`);
  }
}
