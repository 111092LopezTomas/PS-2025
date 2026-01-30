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

}
