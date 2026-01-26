import { Component } from '@angular/core';
import {
  EntradaDto,
  EventGet,
  EventService,
} from '../../services/event.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// Corrige la ubicación de los íconos de Leaflet
delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
  iconUrl: 'assets/leaflet/marker-icon.png',
  shadowUrl: 'assets/leaflet/marker-shadow.png',
});


@Component({
  selector: 'app-event-view',
  imports: [CommonModule],
  templateUrl: './event-view.component.html',
  styleUrl: './event-view.component.css',
})
export class EventViewComponent {
  evento: EventGet = {
    id: 0,
    evento: '',
    entradasDetalle: [],
  } as any;
  loading: boolean = true;
  error: string = '';
  eventoId: number = 0;
  apiBase = 'http://localhost:8080';

  mapaInicializado: boolean = false;

  disponibilidad: number = 0;
  precio: number = 0;

  constructor(
    private eventService: EventService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Obtener ID del evento desde la ruta
    this.route.params.subscribe((params) => {
      this.eventoId = +params['id']; // El '+' convierte string a number
      this.cargarEvento();
    });
  }

  get primeraEntrada() {
    return this.evento.entradasDetalle.length > 0
      ? this.evento.entradasDetalle[0]
      : null;
  }

  cargarEvento(): void {
    this.loading = true;
    this.error = '';

    this.eventService.getEventById(this.eventoId).subscribe({
      next: (data) => {
        this.evento = data;
        this.loading = false;
        if (data.direccion && !this.mapaInicializado) {
          this.mostrarMapa(
            this.evento.direccion.toString(),
            this.evento.establecimiento.toString(),
            this.evento.ciudad.toString()
          );
        }
      },
      error: (err) => {
        this.error =
          'No se pudo cargar el evento. Por favor, intenta nuevamente.';
        this.loading = false;
        console.error('Error al cargar evento:', err);
      },
    });
  }

  mostrarMapa(direccion: string, establecimiento: string, ciudad: string): void {
    // Llamada a Photon para obtener coordenadas
    const url = `https://photon.komoot.io/api/?q=${encodeURIComponent(direccion + ', ' + ciudad)}&limit=1&lang=en`;

    fetch(url)
      .then((res) => res.json())
      .then((data) => {
        if (!data.features || data.features.length === 0) {
          console.warn('No se encontraron coordenadas para la dirección.');
          return;
        }

        const coords = data.features[0].geometry.coordinates;
        const lon = coords[0];
        const lat = coords[1];

        const map = L.map('map').setView([lat, lon], 17);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          attribution: '&copy; OpenStreetMap contributors',
        }).addTo(map);

        L.marker([lat, lon])
          .addTo(map)
          .bindPopup(`<b>${establecimiento}</b><br>${direccion}`)
          .openPopup();

        this.mapaInicializado = true;
      })
      .catch((err) => console.error('Error en geocodificación Photon:', err));
  }

  comprarEntrada(entrada?: EntradaDto): void {
    if (entrada) {
      // Navega con datos de la entrada específica
      this.router.navigate(['/checkout'], {
        queryParams: {
          eventoId: this.eventoId,
          tipo: entrada.id,
          precio: entrada.precio,
        },
      });
    } else {
      // Tu comportamiento original
      this.router.navigate(['/checkout']);
    }
  }

  reintentar(): void {
    this.cargarEvento();
  }

  getPrecioMinimo(): number {
    return this.evento?.entradasDetalle
      ? Math.min(...this.evento.entradasDetalle.map((e) => e.precio))
      : 0;
  }

  getPrecioMaximo(): number {
    return this.evento?.entradasDetalle
      ? Math.max(...this.evento.entradasDetalle.map((e) => e.precio))
      : 0;
  }

  getTotalDisponibilidad(): number {
    return (
      this.evento?.entradasDetalle?.reduce(
        (sum, e) => sum + e.disponibilidad,
        0
      ) || 0
    );
  }

  getCapacidadInicial(entrada: EntradaDto): number {
    return entrada.disponibilidad * 1.5; // Estimación para la barra de progreso
  }

  verEstablecimiento(id: number):void {
    this.router.navigate(['/establecimientos', id]);
  }

  volver() {
    this.router.navigate(['/eventos']);
  }

}
