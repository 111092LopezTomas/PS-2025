import { Component, AfterViewInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { PaymentService } from '../../services/payment.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';

declare const MercadoPago: any; // SDK global

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  imports: [CommonModule, FormsModule],
})
export class CheckoutComponent implements AfterViewInit {
  loading = false;
  error?: string;
  prefId?: string;
  initPoint?: string;
  eventId = 0;
  precio?: number;
  cantidad: number = 1;
  total: number = 0;

  constructor(private payments: PaymentService, private route: ActivatedRoute) {}

  ngOnInit() {
  const id = Number(this.route.snapshot.queryParamMap.get('eventoId'));
    this.eventId = Number.isFinite(id) && id > 0 ? id : 0;

    const p = Number(this.route.snapshot.queryParamMap.get('precio'));
    this.precio = Number.isFinite(p) && p > 0 ? p : undefined;

    this.calcularTotal();
}

  ngAfterViewInit(): void {}

async comprar() {
  try {
    this.error = undefined;
    this.loading = true;

    const res = await this.payments
      .createPreferenceForEvent(this.eventId, this.cantidad, this.precio)
      .toPromise();

    if (!res) throw new Error('Sin respuesta');
    this.prefId = res.preferenceId;
    this.initPoint = res.initPoint;

    const mp = new MercadoPago(environment.mpPublicKey, { locale: 'es-AR' });
    const bricksBuilder = mp.bricks();
    await bricksBuilder.create('wallet', 'wallet_container', {
      initialization: { preferenceId: this.prefId },
      customization: { texts: { valueProp: 'smart_option' } },
    });
  } catch (e: any) {
    this.error = e?.message || 'Error inicializando el pago';
  } finally {
    this.loading = false;
  }
}


  redirigirCheckoutPro() {
    if (this.initPoint) {
      window.location.href = this.initPoint;
    }
  }

  calcularTotal(): number {
    return this.total = this.cantidad * this.precio!;
  }

  onCantidadChange() {
    if (!Number.isFinite(this.cantidad) || this.cantidad < 1) {
      this.cantidad = 1;
    }
    this.calcularTotal();
  }
}
