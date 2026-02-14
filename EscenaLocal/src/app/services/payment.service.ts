import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

export interface CreatePrefCommand {
  externalReference: string;
  items: {
    id: string;
    title: string;
    description: string;
    quantity: number;
    unitPrice: number;
  }[];
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private base = environment.apiBaseLocal;
  constructor(private http: HttpClient) {}

  createPreference(cmd: CreatePrefCommand) {
    return this.http.post<{ preferenceId: string; initPoint: string }>(
      `${this.base}/payments/create-preference`,
      cmd
    );
  }

  createPreferenceForEvent(
  eventId: number,
  tipoEntradaId: number,
  qty = 1,
  precio?: number
) {
  let url = `${environment.apiBaseLocal}/payments/create-preference/event/${eventId}`
          + `?tipoEntradaId=${tipoEntradaId}`
          + `&qty=${qty}`;

  if (precio && precio > 0) {
    url += `&precio=${precio}`;
  }

  return this.http.post<{ preferenceId: string; initPoint: string }>(url, {});
}


}
