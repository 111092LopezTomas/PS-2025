import { Component, AfterViewInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { PaymentService } from '../../services/payment.service';
import { CommonModule } from '@angular/common';

declare const MercadoPago: any; // SDK global

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  imports: [CommonModule],
})
export class CheckoutComponent implements AfterViewInit {
  loading = false;
  error?: string;
  prefId?: string;
  initPoint?: string;

  constructor(private payments: PaymentService) {}

  ngAfterViewInit(): void {}

  async comprar() {
    try {
      this.error = undefined;
      this.loading = true;
      const res = await this.payments
        .createPreference({
          externalReference: 'EVT-0001',
          items: [
            {
              id: 'E1',
              title: 'Entrada x1',
              description: 'Show',
              quantity: 1,
              unitPrice: 1000,
            },
          ],
        })
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
}
