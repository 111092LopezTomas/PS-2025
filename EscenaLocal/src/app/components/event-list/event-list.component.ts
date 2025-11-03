import { Component, OnInit, OnDestroy } from '@angular/core';
import { EventService, EventGet, FiltrosEvento } from '../../services/event.service';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

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
  
  // NUEVO: Variables para controlar el mensaje
  hayFiltrosActivos = false;
  filtroActual: FiltrosEvento = { busqueda: '', provincia: '' };
  
  private destroy$ = new Subject<void>();

  constructor(
    private eventService: EventService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarEventos();

    this.eventService.filtros$
      .pipe(takeUntil(this.destroy$))
      .subscribe(filtros => {
        // Guardar filtros actuales
        this.filtroActual = filtros;
        
        // Determinar si hay filtros activos
        this.hayFiltrosActivos = !!(filtros.busqueda || filtros.provincia);
        
        if (this.todosLosEventos.length > 0) {
          this.filtrarEventos(filtros.busqueda, filtros.provincia);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

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