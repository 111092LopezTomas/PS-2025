import { Component, OnInit, OnDestroy } from '@angular/core';
import { EventService, EventGet, FiltrosEvento } from '../../services/event.service';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

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
  isLogged = false;

  // control de filtros
  hayFiltrosActivos = false;
  filtroActual: FiltrosEvento = { busqueda: '', provincia: '', genero: '' };

  // para saber si estamos viendo eventos de un productor o de un artista
  vistaPorProductor = false;
  vistaPorArtista = false;
  idPersona!: number;

  private destroy$ = new Subject<void>();

  constructor(
    private eventService: EventService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isLogged = this.authService.isLoggedIn();

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

    // 2) escuchamos cambios de filtros
    this.eventService.filtros$
      .pipe(takeUntil(this.destroy$))
      .subscribe(filtros => {
        this.filtroActual = filtros;

        this.hayFiltrosActivos = !!(
          filtros.busqueda ||
          filtros.provincia ||
          filtros.genero
        );

        // solo filtramos cuando tenemos listado base
        if (this.todosLosEventos.length > 0 && !this.vistaPorProductor && !this.vistaPorArtista) {
          this.filtrarEventos(filtros.busqueda, filtros.provincia, filtros.genero);
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
        console.log('EVENTOS CARGADOS:', data);
        this.todosLosEventos = data;
        this.events = data;

        const filtrosActuales = this.eventService.getFiltrosActuales?.() || {
          busqueda: '',
          provincia: '',
          genero: ''
        };

        if (
          filtrosActuales.busqueda ||
          filtrosActuales.provincia ||
          filtrosActuales.genero
        ) {
          this.filtrarEventos(
            filtrosActuales.busqueda,
            filtrosActuales.provincia,
            filtrosActuales.genero
          );
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

  private filtrarEventos(
  busqueda: string,
  provincia: string,
  genero?: string
): void {
  if (!this.todosLosEventos || this.todosLosEventos.length === 0) {
    this.events = [];
    return;
  }

  let resultado = [...this.todosLosEventos];

  // Provincia
  if (provincia) {
    resultado = resultado.filter(e => String(e.provincia) === provincia);
  }

  // Género
  if (genero) {
    const g = genero.trim().toLowerCase();
    resultado = resultado.filter(e => (e.genero || '').trim().toLowerCase().includes(g));
  }

  const bRaw = (busqueda || '').trim();
  const fechaDetectada = this.extraerFechaDesdeBusqueda(bRaw); // YYYY-MM-DD o null

  if (fechaDetectada) {
    resultado = resultado.filter(e => this.eventoEsDeFecha(e, fechaDetectada));
  } else if (bRaw) {
    const b = bRaw.toLowerCase();
    resultado = resultado.filter(e => {
      const artistas = Array.isArray(e.artistas)
        ? e.artistas.join(' ')
        : String(e.artistas || '');

      return artistas.toLowerCase().includes(b) ||
        String(e.evento || '').toLowerCase().includes(b) ||
        String(e.establecimiento || '').toLowerCase().includes(b) ||
        String(e.ciudad || '').toLowerCase().includes(b) ||
        String(e.genero || '').toLowerCase().includes(b);
    });
  }

  this.events = resultado;
}

// ----------------------
// Helpers
// ----------------------

private extraerFechaDesdeBusqueda(input: string): string | null {
  if (!input) return null;

  const s = input.trim().toLowerCase();

  // Opcional: palabras clave
  if (s === 'hoy') return this.formatYMD(new Date());
  if (s === 'mañana' || s === 'manana') {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return this.formatYMD(d);
  }

  // 1) YYYY-MM-DD
  const iso = s.match(/^(\d{4})-(\d{2})-(\d{2})$/);
  if (iso) {
    const y = Number(iso[1]), m = Number(iso[2]), d = Number(iso[3]);
    if (this.esFechaValida(y, m, d)) return `${iso[1]}-${iso[2]}-${iso[3]}`;
    return null;
  }

  // 2) DD/MM/YYYY o DD-MM-YYYY
  const fullLat = s.match(/^(\d{2})[\/-](\d{2})[\/-](\d{4})$/);
  if (fullLat) {
    const d = Number(fullLat[1]), m = Number(fullLat[2]), y = Number(fullLat[3]);
    if (!this.esFechaValida(y, m, d)) return null;
    return `${fullLat[3]}-${fullLat[2]}-${fullLat[1]}`; // YYYY-MM-DD
  }

  // 3) ✅ DD/MM o DD-MM (sin año) → asumimos año actual
  const shortLat = s.match(/^(\d{2})[\/-](\d{2})$/);
  if (shortLat) {
    const d = Number(shortLat[1]);
    const m = Number(shortLat[2]);
    const y = new Date().getFullYear();
    if (!this.esFechaValida(y, m, d)) return null;

    const mm = String(m).padStart(2, '0');
    const dd = String(d).padStart(2, '0');
    return `${y}-${mm}-${dd}`;
  }

  return null;
}
private eventoEsDeFecha(e: EventGet, ymd: string): boolean {
  const fechaEvento = (e.fecha as any);

  // Tu backend manda "YYYY-MM-DD"
  if (typeof fechaEvento === 'string') {
    return fechaEvento.trim() === ymd;
  }

  // fallback si algún día llega Date (por si acaso)
  if (fechaEvento instanceof Date) {
    const yyyy = fechaEvento.getFullYear();
    const mm = String(fechaEvento.getMonth() + 1).padStart(2, '0');
    const dd = String(fechaEvento.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}` === ymd;
  }

  return false;
}

private formatYMD(d: Date): string {
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd}`;
}

private esFechaValida(y: number, m: number, d: number): boolean {
  if (m < 1 || m > 12) return false;
  if (d < 1 || d > 31) return false;
  const dt = new Date(y, m - 1, d);
  return dt.getFullYear() === y && (dt.getMonth() + 1) === m && dt.getDate() === d;
}


  limpiarFiltros(): void {
    this.eventService.actualizarFiltros({
      busqueda: '',
      provincia: '',
      genero: ''
    });
  }

  VerEvento(id: number): void {
    this.router.navigate(['/evento', id]);
  }
}
