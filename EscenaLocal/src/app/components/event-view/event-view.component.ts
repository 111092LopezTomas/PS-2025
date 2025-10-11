import { Component } from '@angular/core';
import { EventGet, EventService } from '../../services/event.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-event-view',
  imports: [CommonModule],
  templateUrl: './event-view.component.html',
  styleUrl: './event-view.component.css'
})
export class EventViewComponent {
  evento: EventGet | null = null;
  loading: boolean = true;
  error: string = '';
  eventoId: number = 0;
  apiBase = 'http://localhost:8080';

  constructor(
    private eventService: EventService,
    private route: ActivatedRoute, 
    private router: Router
  ) { }

  ngOnInit(): void {
    // Obtener ID del evento desde la ruta
    this.route.params.subscribe(params => {
      this.eventoId = +params['id']; // El '+' convierte string a number
      this.cargarEvento();
    });
  }

  cargarEvento(): void {
    this.loading = true;
    this.error = '';

    this.eventService.getEventById(this.eventoId).subscribe({
      next: (data) => {
        this.evento = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'No se pudo cargar el evento. Por favor, intenta nuevamente.';
        this.loading = false;
        console.error('Error al cargar evento:', err);
      }
    });
  }

  comprarEntrada(): void {
    this.router.navigate(['/checkout'])
    /* if (!this.evento) return;

    this.eventService.comprarEntrada(this.evento.id, 1).subscribe({
      next: (response) => {
        alert('¡Compra realizada con éxito!');
        // Recargar evento para actualizar entradas disponibles
        this.cargarEvento();
      },
      error: (err) => {
        alert('Error al procesar la compra. Intenta nuevamente.');
        console.error('Error en compra:', err);
      }
    }); */
  }

  /* compartirEvento(): void {
    if (!this.evento) return;
    
    const url = window.location.href;
    if (navigator.share) {
      navigator.share({
        title: this.evento.evento,
        text: `Mira este evento: ${this.evento.evento}`,
        url: url
      }).catch(err => console.log('Error al compartir:', err));
    } else {
      navigator.clipboard.writeText(url);
      alert('Link copiado al portapapeles');
    }
  } */

  reintentar(): void {
    this.cargarEvento();
  }
}
