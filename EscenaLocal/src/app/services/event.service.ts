import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

export interface EntradaDto {
  id: number;
  tipo: string;
  precio: number;
  disponibilidad: number;
}

export interface Artista {
  id: number;
  nombre: String;
}

export interface EventGet {
  entradas(entradas: any): unknown;
  id: number;
  activo: Boolean;
  descripcion: string;
  evento: string;
  fecha: Date;
  hora: Date;
  artistas: string[];
  clasificacion: string;
  establecimientoId: number;
  establecimiento: string;
  barrio: string;
  ciudad: string;
  provincia: string;
  imagen: string;
  direccion: string;
  capacidad: number;
  genero: string;
  duracion: string;
  destacados: string[];
  entradasDetalle: EntradaDto[];
  productor: string;
  productorId: number;
}

export interface FiltrosEvento {
  busqueda: string;
  provincia: string;
  genero: string;
}

export interface GeneroMusical {
  id: number;
  genero: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8080';

  // Sistema de filtros
  private filtrosSubject = new BehaviorSubject<FiltrosEvento>({
    busqueda: '',
    provincia: '',
    genero: ''
  });
  public filtros$: Observable<FiltrosEvento> = this.filtrosSubject.asObservable();

  private cargandoSubject = new BehaviorSubject<boolean>(false);
  public cargando$: Observable<boolean> = this.cargandoSubject.asObservable();

  constructor(private http: HttpClient) {}

  // ========== TUS MÉTODOS ORIGINALES ==========
  getEvents(): Observable<EventGet[]> {
    return this.http.get<EventGet[]>(this.apiUrl + "/eventos/all");
  }

  getImagenEvento(id: number): Observable<Blob> {
    return this.http.get(`http://localhost:8080/eventos/${id}/imagen`, {
      responseType: 'blob'
    });
  }

  getArtistas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/artistas/all`);
  }

  getProductores(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/productores/all`);
  }

  getTiposEntrada(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/entradas/all`);
  }

  getEstablecimientos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/establecimientos/all`);
  }

  getClasificaciones(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clasificaciones/all`);
  }

  getProvincias(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/provincias/all`);
  }

  getGenerosMusicales(): Observable<GeneroMusical[]> {
  return this.http.get<GeneroMusical[]>(`${this.apiUrl}/generos/all`);
}

  crearEvento(evento: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/eventos/nuevo`, evento);
  }

  getEventById(id: number): Observable<EventGet> {
    return this.http.get<EventGet>(`${this.apiUrl}/eventos/${id}`);
  }

  comprarEntrada(eventoId: number, cantidad: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/compras`, {
      eventoId,
      cantidad
    });
  }

  // ========== NUEVOS MÉTODOS PARA FILTROS ==========
  
  // Método para actualizar los filtros desde el searchbox
  actualizarFiltros(filtros: FiltrosEvento): void {
    this.filtrosSubject.next(filtros);
  }

  // Método para obtener filtros actuales (IMPORTANTE para evitar el error)
  getFiltrosActuales(): FiltrosEvento {
    return this.filtrosSubject.value;
  }

  // Método para filtrar eventos localmente (en el navegador)
  filtrarEventosLocalmente(eventos: EventGet[], filtros: FiltrosEvento): EventGet[] {
    if (!eventos || eventos.length === 0) {
      return [];
    }

    let resultados = eventos;

    // Filtrar por provincia
    if (filtros.provincia && filtros.provincia.trim() !== '') {
      resultados = resultados.filter(evento => 
        String(evento.provincia) === filtros.provincia
      );
    }

    // Filtrar por búsqueda de texto
    if (filtros.busqueda && filtros.busqueda.trim() !== '') {
      const busquedaLower = filtros.busqueda.toLowerCase();
      resultados = resultados.filter(evento => {
        const artistas = Array.isArray(evento.artistas)
          ? evento.artistas.map(a => String(a)).join(' ')
          : String(evento.artistas || '');

        return artistas.toLowerCase().includes(busquedaLower) ||
               String(evento.evento || '').toLowerCase().includes(busquedaLower) ||
               String(evento.establecimiento || '').toLowerCase().includes(busquedaLower) ||
               String(evento.ciudad || '').toLowerCase().includes(busquedaLower) ||
               String(evento.genero || '').toLowerCase().includes(busquedaLower) ||
               String(evento.clasificacion || '').toLowerCase().includes(busquedaLower) ||
               String(evento.productor || '').toLowerCase().includes(busquedaLower);
      });
    }

    return resultados;
  }

  // Método alternativo: obtener eventos filtrados desde el backend
  getEventosFiltrados(filtros: FiltrosEvento): Observable<EventGet[]> {
    this.cargandoSubject.next(true);
    
    const params: any = {};
    if (filtros.busqueda) {
      params.busqueda = filtros.busqueda;
    }
    if (filtros.provincia) {
      params.provincia = filtros.provincia;
    }

    return new Observable(observer => {
      this.http.get<EventGet[]>(`${this.apiUrl}/eventos/all`, { params })
        .subscribe({
          next: (eventos) => {
            this.cargandoSubject.next(false);
            observer.next(eventos);
            observer.complete();
          },
          error: (error) => {
            this.cargandoSubject.next(false);
            observer.error(error);
          }
        });
    });
  }

  /**
   * Actualiza un evento existente
   */
  actualizarEvento(id: number, formData: FormData): Observable<any> {
    return this.http.put(`${this.apiUrl}/eventos/editar/${id}`, formData);
  }

  getEventosByEstablecimiento(id: number): Observable<EventGet> {
    return this.http.get<EventGet>(`${this.apiUrl}/eventos/establecimientos/${id}`);
  }

  getEventsByProductor(id: number): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/eventos/productores/${id}`);
}

getEventsByArtista(id: number): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/eventos/artistas/${id}`);
}
}