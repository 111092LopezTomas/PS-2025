import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class TicketService {
  private apiBase = environment.apiBaseLocal;

  constructor(private http: HttpClient) {}

  misCompras() {
    return this.http.get<any[]>(`${this.apiBase}/tickets/mis-compras`);
  }

  qrUrl(ventaId: number) {
    return `${this.apiBase}/ventas/${ventaId}/qr`;
  }

  getQrBlob(ventaId: number) {
    return this.http.get(`${this.apiBase}/ventas/${ventaId}/qr`, { responseType: 'blob' });
  }
}

