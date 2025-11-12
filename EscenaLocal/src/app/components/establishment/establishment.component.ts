import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EstablishmentService } from '../../services/establishment.service';
//agregado
import * as L from 'leaflet';
import 'leaflet/dist/leaflet.css';

//agregado
// Corrige la ubicación de los íconos de Leaflet
delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'assets/leaflet/marker-icon-2x.png',
  iconUrl: 'assets/leaflet/marker-icon.png',
  shadowUrl: 'assets/leaflet/marker-shadow.png',
});

export interface EstablecimientoDetalle {
  id: number;
  establecimiento: string;
  direccion: string;
  capacidad: number;
  barrio: string;
  ciudad: string;
  provincia: string;
}

@Component({
  selector: 'app-establishment',
  imports: [CommonModule],
  templateUrl: './establishment.component.html',
  styleUrl: './establishment.component.css',
})
export class EstablishmentComponent implements OnInit {
  establecimiento: EstablecimientoDetalle | null = null;
  loading = true;
  error: string | null = null;

  //agregado
  mapaInicializado: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private establishmentService: EstablishmentService
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading = true;
    this.error = null;

    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'No se especificó un establecimiento válido.';
      this.loading = false;
      return;
    }

    this.establishmentService.getEstablecimientoById(id).subscribe({
      next: (data) => {
        this.establecimiento = data;
        this.loading = false;
        if (data.direccion && !this.mapaInicializado) {
          this.mostrarMapa(data.direccion, data.establecimiento, data.ciudad);
        }
      },
      error: (err) => {
        console.error('Error al obtener establecimiento', err);
        this.error = 'No se pudo cargar el establecimiento.';
        this.loading = false;
      },
    });
  }

  //agregado
  mostrarMapa(
    direccion: string,
    establecimiento: string,
    ciudad: string
  ): void {
    // Llamada a Photon para obtener coordenadas
    const url = `https://photon.komoot.io/api/?q=${encodeURIComponent(
      direccion + ', ' + ciudad
    )}&limit=1&lang=en`;

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

  volver(): void {
    history.back();
  }
}
