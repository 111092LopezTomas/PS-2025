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
  private base = environment.apiBase;
  constructor(private http: HttpClient) {}

  createPreference(cmd: CreatePrefCommand) {
    return this.http.post<{ preferenceId: string; initPoint: string }>(
      `${this.base}/payments/create-preference`,
      cmd
    );
  }
}
