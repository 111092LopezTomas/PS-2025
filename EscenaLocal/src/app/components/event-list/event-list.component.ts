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
  isProductor = false;

  /** ✅ id del productor logueado (para validar propiedad) */
  productorIdLogueado: number | null = null;
  

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
    console.log("TOKEN " + this.authService.getToken());
    this.isLogged = this.authService.isLoggedIn();

// ✅ tu token trae "ROL_PRODUCTOR", así que esto tiene que matchear eso
this.isProductor = this.authService.tieneRol('ROL_PRODUCTOR');

// ✅ traer el productorId guardado desde el perfil
this.productorIdLogueado = this.authService.getProductorId();

// 🔥 logs para debug
console.log('isLogged:', this.isLogged);
console.log('isProductor:', this.isProductor);
console.log('productorIdLogueado (localStorage):', this.productorIdLogueado);

    // ✅ detectar rol + productorId
    this.isProductor = this.authService.tieneRol('PRODUCTOR');
this.productorIdLogueado = this.authService.getProductorIdFromToken();

    // 1) vemos qué ruta es
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      const path = this.route.snapshot.routeConfig?.path || '';

      this.vistaPorProductor = path.includes('productor');
      this.vistaPorArtista = path.includes('artista');

      if (id) {
        this.idPersona = Number(id);
      }

      if (this.vistaPorProductor && this.idPersona) {
        this.cargarEventosPorProductor(this.idPersona);
      } else if (this.vistaPorArtista && this.idPersona) {
        this.cargarEventosPorArtista(this.idPersona);
      } else {
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
  // ✅ PERMISOS EDICIÓN
  // ======================

  /** Devuelve true si el productor logueado es dueño del evento */
 puedeEditarEvento(e: EventGet): boolean {
  const productorId = this.authService.getProductorId();
  const rol = this.authService.getUserRoleFromToken() || this.authService.getRoleFromToken();

  const esProductor = String(rol || '').trim().toUpperCase() === 'ROL_PRODUCTOR';

  return (
    this.authService.isLoggedIn() &&
    esProductor &&
    !!productorId &&
    Number((e as any).productorId) === Number(productorId)
  );
}


  editarEvento(id: number, e: EventGet): void {
  if (!this.puedeEditarEvento(e)) return;
  this.router.navigate(['/eventos/editar', id]);
}

  /** Ajustá estas 2 funciones a tu AuthService real */
  private detectarSiEsProductor(): boolean {
    // Opción A: si ya tenés método
    if ((this.authService as any).isProductor) return (this.authService as any).isProductor();

    // Opción B: si tenés getRole()
    if ((this.authService as any).getRole) {
      const role = (this.authService as any).getRole();
      return String(role || '').toUpperCase().includes('PRODUCTOR');
    }

    // Opción C: fallback desde JWT (role/roles)
    const token = (this.authService as any).getToken?.() || localStorage.getItem('token');
    const payload = this.decodeJwtPayload(token);
    const roles = payload?.roles ?? payload?.role ?? payload?.authorities ?? [];
    const str = Array.isArray(roles) ? roles.join(' ') : String(roles);
    return str.toUpperCase().includes('PRODUCTOR');
  }

  private obtenerProductorIdLogueado(): number | null {
    // Opción A: si ya lo tenés directo
    if ((this.authService as any).getProductorId) return Number((this.authService as any).getProductorId());

    // Opción B: desde JWT claim (ej: productorId)
    const token = (this.authService as any).getToken?.() || localStorage.getItem('token');
    const payload = this.decodeJwtPayload(token);

    const id =
      payload?.productorId ??
      payload?.idProductor ??
      payload?.productor_id ??
      null;

    return id != null ? Number(id) : null;
  }

  private decodeJwtPayload(token: string | null): any | null {
    try {
      if (!token) return null;
      const base64 = token.split('.')[1];
      if (!base64) return null;
      const json = decodeURIComponent(
        atob(base64.replace(/-/g, '+').replace(/_/g, '/'))
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(json);
    } catch {
      return null;
    }
  }

  // ======================
  // CARGAS
  // ======================

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

        if (filtrosActuales.busqueda || filtrosActuales.provincia || filtrosActuales.genero) {
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
  // FILTROS (sin cambios)
  // ======================

  private filtrarEventos(busqueda: string, provincia: string, genero?: string): void {
    if (!this.todosLosEventos || this.todosLosEventos.length === 0) {
      this.events = [];
      return;
    }

    let resultado = [...this.todosLosEventos];

    if (provincia) {
      resultado = resultado.filter(e => String((e as any).provincia) === provincia);
    }

    if (genero) {
      const g = genero.trim().toLowerCase();
      resultado = resultado.filter(e => (String((e as any).genero || '')).trim().toLowerCase().includes(g));
    }

    const bRaw = (busqueda || '').trim();
    const fechaDetectada = this.extraerFechaDesdeBusqueda(bRaw);

    if (fechaDetectada) {
      resultado = resultado.filter(e => this.eventoEsDeFecha(e, fechaDetectada));
    } else if (bRaw) {
      const b = bRaw.toLowerCase();
      resultado = resultado.filter(e => {
        const artistas = Array.isArray((e as any).artistas)
          ? (e as any).artistas.join(' ')
          : String((e as any).artistas || '');

        return artistas.toLowerCase().includes(b) ||
          String((e as any).evento || '').toLowerCase().includes(b) ||
          String((e as any).establecimiento || '').toLowerCase().includes(b) ||
          String((e as any).ciudad || '').toLowerCase().includes(b) ||
          String((e as any).genero || '').toLowerCase().includes(b);
      });
    }

    this.events = resultado;
  }

  // Helpers (sin cambios)
  private extraerFechaDesdeBusqueda(input: string): string | null { /* ...tu código... */ return null; }
  private eventoEsDeFecha(e: EventGet, ymd: string): boolean { /* ...tu código... */ return false; }
  private formatYMD(d: Date): string { /* ...tu código... */ return ''; }
  private esFechaValida(y: number, m: number, d: number): boolean { /* ...tu código... */ return true; }

  limpiarFiltros(): void {
    this.eventService.actualizarFiltros({ busqueda: '', provincia: '', genero: '' });
  }

  VerEvento(id: number): void {
    this.router.navigate(['/evento', id]);
  }
}
