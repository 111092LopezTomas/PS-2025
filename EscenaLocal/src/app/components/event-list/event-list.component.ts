import { Component, OnInit, OnDestroy } from '@angular/core';
import { EventService, EventGet, FiltrosEvento } from '../../services/event.service';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, DatePipe, RouterModule],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.css']
})
export class EventListComponent implements OnInit, OnDestroy {
  events: EventGet[] = [];
  todosLosEventos: EventGet[] = [];
  apiBase = 'http://localhost:8080';

  // control de filtros
  hayFiltrosActivos = false;
  filtroActual: FiltrosEvento = { busqueda: '', provincia: '' };

  // para saber si estamos viendo eventos de un productor o de un artista
  vistaPorProductor = false;
  vistaPorArtista = false;
  idPersona!: number;

  private destroy$ = new Subject<void>();

  constructor(
    private eventService: EventService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // 1) vemos qué ruta es
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      const path = this.route.snapshot.routeConfig?.path || '';

      this.vistaPorProductor = path.includes('productor');
      this.vistaPorArtista = path.includes('artista');

      if (id) {
        this.idPersona = Number(id);
      }

      // según el tipo de ruta, cargamos de un lado u otro
      if (this.vistaPorProductor && this.idPersona) {
        this.cargarEventosPorProductor(this.idPersona);
      } else if (this.vistaPorArtista && this.idPersona) {
        this.cargarEventosPorArtista(this.idPersona);
      } else {
        // ruta /eventos normal
        this.cargarEventos();
      }
    });

    // 2) escuchamos cambios de filtros (esto sigue igual)
    this.eventService.filtros$
      .pipe(takeUntil(this.destroy$))
      .subscribe(filtros => {
        this.filtroActual = filtros;
        this.hayFiltrosActivos = !!(filtros.busqueda || filtros.provincia);

        // solo filtramos cuando tenemos listado base
        if (this.todosLosEventos.length > 0 && !this.vistaPorProductor && !this.vistaPorArtista) {
          this.filtrarEventos(filtros.busqueda, filtros.provincia);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // ======================
  // CARGAS
  // ======================

  // lista general
  cargarEventos(): void {
    this.eventService.getEvents().subscribe({
      next: (data) => {
        this.todosLosEventos = data;
        this.events = data;

        const filtrosActuales = this.eventService.getFiltrosActuales?.() || { busqueda: '', provincia: '' };
        if (filtrosActuales.busqueda || filtrosActuales.provincia) {
          this.filtrarEventos(filtrosActuales.busqueda, filtrosActuales.provincia);
        }
      },
      error: (err) => {
        console.error('Error al cargar eventos:', err);
        this.todosLosEventos = [];
        this.events = [];
      }
    });
  }

  // lista por productor
  private cargarEventosPorProductor(productorId: number): void {
    this.eventService.getEventsByProductor(productorId).subscribe({
      next: (data) => {
        // en este caso NO aplicamos los filtros globales porque ya viene filtrado por productor
        this.events = data;
        this.todosLosEventos = data;
      },
      error: (err) => {
        console.error('Error al cargar eventos del productor:', err);
        this.events = [];
      }
    });
  }

  // lista por artista
  private cargarEventosPorArtista(artistaId: number): void {
    this.eventService.getEventsByArtista(artistaId).subscribe({
      next: (data) => {
        this.events = data;
        this.todosLosEventos = data;
      },
      error: (err) => {
        console.error('Error al cargar eventos del artista:', err);
        this.events = [];
      }
    });
  }

  // ======================
  // FILTROS
  // ======================

  private filtrarEventos(busqueda: string, provincia: string): void {
    if (!this.todosLosEventos || this.todosLosEventos.length === 0) {
      this.events = [];
      return;
    }

    let resultado = [...this.todosLosEventos];

    if (provincia) {
      resultado = resultado.filter(e => String(e.provincia) === provincia);
    }

    if (busqueda) {
      const b = busqueda.toLowerCase();
      resultado = resultado.filter(e => {
        const artistas = Array.isArray(e.artistas)
          ? e.artistas.join(' ')
          : String(e.artistas || '');

        return artistas.toLowerCase().includes(b) ||
               String(e.evento || '').toLowerCase().includes(b) ||
               String(e.establecimiento || '').toLowerCase().includes(b) ||
               String(e.ciudad || '').toLowerCase().includes(b);
      });
    }

    this.events = resultado;
  }

  limpiarFiltros(): void {
    this.eventService.actualizarFiltros({ busqueda: '', provincia: '' });
  }

  VerEvento(id: number): void {
    this.router.navigate(['/evento', id]);
  }
}
