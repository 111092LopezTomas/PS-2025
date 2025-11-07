import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  // la que ya usabas
  private authUrl = 'http://localhost:8080/auth';
  // para artistas/productores/géneros
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  // ========== los que ya tenías ==========
  obtenerImagen(usuarioId: number): Observable<Blob> {
    return this.http.get(`${this.authUrl}/${usuarioId}/imagen`, {
      responseType: 'blob',
    });
  }

  subirImagen(usuarioId: number, file: File): Observable<void> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<void>(`${this.authUrl}/${usuarioId}/imagen`, formData);
  }

  getUsuarioById(id: number): Observable<any> {
    return this.http.get<any>(`${this.authUrl}/usuarios/${id}`);
  }

  // ========== nuevos métodos ==========

  // para el select de géneros cuando elige ARTISTA
  getGeneros(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/generos/all`);
  }

  // crear la parte de Artista después de registrar el usuario
  crearArtista(data: any): Observable<any> {
    // el backend debería tener algo como POST /api/artistas
    return this.http.post<any>(`${this.authUrl}/register/art-prod`, data);
  }

  // crear la parte de Productor después de registrar el usuario
  crearProductor(data: any): Observable<any> {
    // el backend debería tener algo como POST /api/productores
    return this.http.post<any>(`${this.authUrl}/register/art-prod`, data);
  }

  updateUsuarioJson(id: number, dto: any) {
  return this.http.put<void>(`${this.authUrl}/usuarios/${id}`, dto);
}

subirImagenUsuario(id: number, file: File) {
  const fd = new FormData();
  fd.append('file', file);
  return this.http.post<void>(`${this.authUrl}/${id}/imagen`, fd);
}
}
