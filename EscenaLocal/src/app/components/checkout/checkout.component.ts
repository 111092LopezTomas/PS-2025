import { Component, AfterViewInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { PaymentService } from '../../services/payment.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { loadMercadoPago } from '@mercadopago/sdk-js';
 

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  imports: [CommonModule, FormsModule],
  // si es standalone, recordá agregar: standalone: true,
})
export class CheckoutComponent implements AfterViewInit {
  loading = false;
  error?: string;
  prefId?: string;
  tipoEntradaId: number = 1 ;
  initPoint?: string;
  eventId = 0;
  precio?: number;
  cantidad: number = 1;
  total: number = 0;

  // guardamos la promesa del SDK para no cargarlo mil veces
  private mpPromise?: Promise<any>;

  constructor(
    private payments: PaymentService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.queryParamMap.get('eventoId'));
    this.eventId = Number.isFinite(id) && id > 0 ? id : 0;

    const p = Number(this.route.snapshot.queryParamMap.get('precio'));
    this.precio = Number.isFinite(p) && p > 0 ? p : undefined;

    this.calcularTotal();
  }

  ngAfterViewInit(): void {
    // En tu versión del SDK, loadMercadoPago NO recibe argumentos
    console.log('[MP] Cargando SDK con loadMercadoPago()…');
    this.mpPromise = loadMercadoPago().catch(err => {
      console.error('[MP] Error cargando SDK:', err);
      this.error = 'No se pudo cargar el SDK de Mercado Pago';
      throw err;
    });
  }

  async comprar() {
    try {
      this.error = undefined;
      this.loading = true;

      if (!this.precio || this.precio <= 0) {
        throw new Error('Precio inválido');
      }

      if (!environment.mpPublicKey) {
        throw new Error('mpPublicKey no configurada en environment');
      }

      // 1) Crear preferencia en tu backend
      console.log('[MP] Creando preferencia para evento', this.eventId);
      const res = await this.payments
        .createPreferenceForEvent(this.eventId, this.tipoEntradaId, this.cantidad, this.precio)
        .toPromise();

      if (!res) throw new Error('Sin respuesta del backend al crear preferencia');

      this.prefId = res.preferenceId;
      this.initPoint = res.initPoint;

      console.log('[MP] Preferencia creada:', {
        preferenceId: this.prefId,
        initPoint: this.initPoint,
      });

      // 2) Esperar a que el SDK de MP esté cargado (script en window)
      console.log('[MP] Esperando a que cargue el SDK…');
      await (this.mpPromise ?? loadMercadoPago());

      // 3) Verificar que window.MercadoPago exista
      const MPConstructor = (window as any).MercadoPago;
      if (!MPConstructor) {
        console.error('[MP] window.MercadoPago es undefined');
        throw new Error('SDK de Mercado Pago no inicializado en window');
      }

      // 4) Crear instancia de MercadoPago usando la PUBLIC KEY
      console.log('[MP] Creando instancia de MercadoPago con publicKey:', environment.mpPublicKey);
      const mp = new MPConstructor(environment.mpPublicKey, {
        locale: 'es-AR',
      });

      // 5) Verificar container del Wallet
      const containerId = 'wallet_container';
      const container = document.getElementById(containerId);
      if (!container) {
        console.error(`[MP] No se encontró el contenedor #${containerId} en el DOM`);
        throw new Error('No se encontró el contenedor para el botón de pago');
      }

      // limpiar contenedor por si hubo intentos previos
      container.innerHTML = '';

      // 6) Construir el Wallet Brick
      const bricksBuilder = mp.bricks();
      console.log('[MP] Renderizando Wallet Brick en', containerId);

      await bricksBuilder.create('wallet', containerId, {
        initialization: {
          preferenceId: this.prefId,
        },
        customization: {
          texts: {
            valueProp: 'smart_option',
          },
        },
        callbacks: {
          onReady: () => {
            console.log('✅ Wallet Brick listo');
          },
          onSubmit: (formData: any) => {
            console.log('💳 onSubmit Wallet (datos que MP envía):', formData);
          },
          onError: (error: any) => {
            console.error('❌ Error en Wallet Brick:', error);
            this.error = 'Error mostrando el botón de pago';
          },
        },
      });
    } catch (e: any) {
      console.error('❌ Error en comprar():', e);
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
    return (this.total = this.cantidad * (this.precio ?? 0));
  }

  onCantidadChange() {
    if (!Number.isFinite(this.cantidad) || this.cantidad < 1) {
      this.cantidad = 1;
    }
    this.calcularTotal();
  }
}
