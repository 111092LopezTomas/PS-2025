import { Component } from '@angular/core';
import { TicketService } from '../../services/ticket.service';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tickets-historial',
  imports: [CommonModule, RouterModule],
  templateUrl: './tickets-historial.component.html',
  styleUrl: './tickets-historial.component.css'
})
export class TicketsHistorialComponent {

  misEntradas: any[] = [];
  loading = false;
  error?: string;

  showQrModal = false;
  selectedQrUrl: string | null = null;
  selectedEntrada: any | null = null;

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading = true;
    this.error = undefined;

    this.ticketService.misCompras().subscribe({
      next: data => {
        this.misEntradas = data || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.error = 'No se pudieron cargar tus entradas';
        this.loading = false;
      }
    });
  }

  trackByVentaId = (_: number, item: any) => item?.ventaId;

  verQr(entrada: any) {
    console.log('Entrada:', entrada);
    this.selectedEntrada = entrada;
    this.selectedQrUrl = this.ticketService.qrUrl(entrada.ventaId);
    this.showQrModal = true;
  }

  cerrarModal() {
    this.showQrModal = false;
    this.selectedQrUrl = null;
    this.selectedEntrada = null;
  }

}
