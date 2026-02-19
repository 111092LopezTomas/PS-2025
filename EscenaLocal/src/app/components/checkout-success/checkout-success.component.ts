import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-checkout-success',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './checkout-success.component.html',
  styleUrls: ['./checkout-success.component.css'],
})
export class CheckoutSuccessComponent implements OnInit {
  paymentId?: string;
  status?: string;
  loading = true;
  error?: string;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.paymentId =
      this.route.snapshot.queryParamMap.get('payment_id') ?? undefined;

    if (!this.paymentId) {
      this.error = 'No se recibió el ID del pago';
      this.loading = false;
      return;
    }

    const API_BASE = 'http://localhost:8080';

    this.http
      .get<{ status: string }>(`${API_BASE}/payments/status/${this.paymentId}`)

      .subscribe({
        next: (res) => {
          console.log("RESPUESTA STATUS:", res);
          this.status = res.status;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.error = 'No se pudo consultar el estado del pago';
          this.loading = false;
        },
      });
  }

  volverInicio() {
    this.router.navigate(['/eventos']);
  }
}
