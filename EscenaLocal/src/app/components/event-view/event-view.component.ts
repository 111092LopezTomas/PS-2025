import { Component } from '@angular/core';
import { EventGet, EventService } from '../../services/event.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

declare const google: any; //  Para Google Maps

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

    //  bandera para saber si el mapa ya se inicializó
  mapaInicializado: boolean = false; 

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
          //  Inicializar el mapa solo cuando se cargó el evento
        if (!this.mapaInicializado) {
          this.initMap(); 
          this.mapaInicializado = true; 
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

  comprarEntrada(): void {
    this.router.navigate(['/checkout']);
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

  //  Método para inicializar Google Maps
  initMap(): void { 
    if (!this.evento) return; 
    //  : Concatenar dirección completa para geocodificación
    const direccionCompleta = `${this.evento.direccion}, ${this.evento.barrio}, Ciudad, País`; // AGREGADO: reemplazar Ciudad/País si es necesario

    const geocoder = new google.maps.Geocoder(); 
    geocoder.geocode({ address: direccionCompleta }, (results: any, status: any) => { // AGREGADO
      if (status === 'OK') { 
        const location = results[0].geometry.location; 

        // Crear mapa
        const map = new google.maps.Map(document.getElementById('map'), { // AGREGADO
          zoom: 15, 
          center: location 
        }); 

        // Agregar marcador
        new google.maps.Marker({ 
          position: location, 
          map: map, 
          title: this.evento.establecimiento 
        }); 
      } else { 
        console.error('No se pudo encontrar la ubicación: ' + status); // AGREGADO
      } 
    }); 
  } 
}
