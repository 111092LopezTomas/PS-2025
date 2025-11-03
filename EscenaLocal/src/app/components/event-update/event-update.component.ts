import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';

import { EventGet, EventService } from '../../services/event.service';

@Component({
  selector: 'app-event-edit',
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './event-update.component.html',
  styleUrl: './event-update.component.css',
})
export class EventUpdateComponent {
  eventForm: FormGroup;

  // catálogos
  artistas: any[] = [];
  productores: any[] = [];
  tiposEntrada: any[] = [];
  establecimientos: any[] = [];
  clasificaciones: any[] = [];

  // ui state
  isLoading = false;
  submitted = false;

  // imagen
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  imagenActual: string | null = null;

  // artistas seleccionados en este evento (array de IDs numéricos)
  artistasSeleccionados: number[] = [];
  artistaSeleccionado: string = ''; // valor temporal del <select> para agregar artista

  // entradas / tickets seleccionadas para este evento
  tiposEntradaSeleccionados: any[] = [];
  tipoEntradaSeleccionado: string = '';
  precioEntrada: number | null = null;
  disponibilidadEntrada: number | null = null;

  eventoId!: number;

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.eventForm = this.fb.group({
      nombreEvento: ['', [Validators.required, Validators.minLength(3)]],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      productorId: ['', Validators.required],
      establecimientoId: ['', Validators.required],
      clasificacionId: ['', Validators.required],
      fecha: ['', Validators.required],
      hora: ['', Validators.required],
      imagen: [''],
      activo: [true],
      artistaId: this.fb.control<number[]>([]), // no lo usamos directo en la vista, pero lo dejamos coherente
    });
  }

  // =========================
  // Ciclo de vida / carga inicial
  // =========================
  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.eventoId = +params['id'];

      if (!this.eventoId || isNaN(this.eventoId)) {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se proporcionó un ID de evento válido',
          confirmButtonText: 'Entendido',
        }).then(() => {
          this.volver();
        });
        return;
      }

      this.cargarTodo();
    });
  }

  /**
   * Trae en paralelo:
   * - el evento
   * - todos los catálogos (artistas, productores, etc)
   * y recién después popula el form + artistasSeleccionados + tiposEntradaSeleccionados.
   */
  private cargarTodo(): void {
    this.isLoading = true;

    forkJoin({
      evento: this.eventService.getEventById(this.eventoId),
      artistas: this.eventService.getArtistas(),
      productores: this.eventService.getProductores(),
      tiposEntrada: this.eventService.getTiposEntrada(),
      establecimientos: this.eventService.getEstablecimientos(),
      clasificaciones: this.eventService.getClasificaciones(),
    }).subscribe({
      next: ({
        evento,
        artistas,
        productores,
        tiposEntrada,
        establecimientos,
        clasificaciones,
      }) => {
        // guardo catálogos en memoria para combos
        this.artistas = artistas;
        this.productores = productores;
        this.tiposEntrada = tiposEntrada;
        this.establecimientos = establecimientos;
        this.clasificaciones = clasificaciones;

        // mapear IDs reales de productor / establecimiento / clasificacion
        const productorId = this.mapNombreToId(
          evento.productor,
          this.productores,
          'nombre'
        );

        const establecimientoId = this.mapNombreToId(
          evento.establecimiento,
          this.establecimientos,
          'establecimiento'
        );

        const clasificacionId = this.mapNombreToId(
          evento.clasificacion,
          this.clasificaciones,
          'clasificacion'
        );

        // rellenar formulario principal
        this.eventForm.patchValue({
          nombreEvento: evento.evento,
          descripcion: evento.descripcion,
          productorId: productorId ?? '',
          establecimientoId: establecimientoId ?? '',
          clasificacionId: clasificacionId ?? '',
          fecha: evento.fecha, // "2025-10-03"
          hora: evento.hora, // "21:30"
          activo: evento.activo,
        });

        // artistasSeleccionados = IDs reales según los nombres que vinieron en `evento.artistas`
        // backend: evento.artistas = ["Artista1", "Artista2", ...]
        this.artistasSeleccionados = this.artistas
          .filter(
            (a) =>
              Array.isArray(evento.artistas) &&
              evento.artistas.includes(a.nombre)
          )
          .map((a) => a.id);

        // Tipos de entrada ya configurados en el evento
        // backend: entradasDetalle = [{ tipo:"VIP", precio:7000, disponibilidad:50 }]
        this.tiposEntradaSeleccionados = Array.isArray(evento.entradasDetalle)
          ? evento.entradasDetalle.map((entrada: any) => {
              const encontrado = this.matchEntradaToCatalogo(entrada);
              return {
                tipoEntradaId: encontrado ? encontrado.id : null,
                nombre: entrada.tipo,
                precio: entrada.precio,
                disponibilidad: entrada.disponibilidad,
              };
            })
          : [];

        // imagen actual: tu JSON no la trae, así que no seteamos.
        // si más adelante el backend manda base64 en evento.imagen:
        // this.imagenActual = evento.imagen;
        // this.imagePreview = evento.imagen;

        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar data inicial:', error);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo cargar la información del evento',
          confirmButtonText: 'Entendido',
        }).then(() => {
          this.volver();
        });
      },
    });
  }

  // =========================
  // Helpers de mapeo catálogos -> IDs
  // =========================

  /**
   * Dado el nombre textual que manda el backend (ej "Productora1")
   * busca el item correspondiente del catálogo y devuelve su id.
   * listaCatalogo es por ej this.productores
   * campoNombreCatalogo es el atributo que contiene el nombre visible en ese catálogo ("nombre", "establecimiento", "clasificacion", etc.)
   */
  private mapNombreToId(
    nombreDelEvento: string,
    listaCatalogo: any[],
    campoNombreCatalogo: string
  ): number | undefined {
    if (!nombreDelEvento) return undefined;
    const encontrado = listaCatalogo.find(
      (item) => item[campoNombreCatalogo] === nombreDelEvento
    );
    return encontrado ? encontrado.id : undefined;
  }

  /**
   * Busca un tipo de entrada del catálogo `this.tiposEntrada`
   * que coincida con la info del evento.
   * - entrada.tipo viene como string legible ("VIP")
   * - en catálogo tenemos objetos tipo { id: 12, entrada: "VIP" }
   */
  private matchEntradaToCatalogo(entradaEvento: any) {
    if (!entradaEvento) return null;
    return this.tiposEntrada.find((t) => t.entrada === entradaEvento.tipo);
  }

  // =========================
  // Artistas (UI add/remove)
  // =========================

  agregarArtista(): void {
    if (!this.artistaSeleccionado) return;

    const artistaId = Number(this.artistaSeleccionado);

    if (!this.artistasSeleccionados.includes(artistaId)) {
      this.artistasSeleccionados.push(artistaId);
      this.artistaSeleccionado = '';
    }
  }

  eliminarArtista(index: number): void {
    this.artistasSeleccionados.splice(index, 1);
  }

  getNombreArtista(id: number): string {
    const artista = this.artistas.find((a) => a.id === id);
    return artista ? artista.nombre : 'Desconocido';
  }

  artistasDisponibles(): any[] {
    return this.artistas.filter(
      (artista) => !this.artistasSeleccionados.includes(artista.id)
    );
  }

  // =========================
  // Tipos de entrada (UI add/remove)
  // =========================

  agregarTipoEntrada(): void {
    if (
      !this.tipoEntradaSeleccionado ||
      !this.precioEntrada ||
      !this.disponibilidadEntrada
    ) {
      Swal.fire({
        icon: 'warning',
        title: 'Campos incompletos',
        text: 'Debes completar el tipo de entrada, precio y disponibilidad',
        confirmButtonText: 'Entendido',
      });
      return;
    }

    const tipoId = Number(this.tipoEntradaSeleccionado);

    if (
      this.tiposEntradaSeleccionados.some((e) => e.tipoEntradaId === tipoId)
    ) {
      Swal.fire({
        icon: 'info',
        title: 'Entrada duplicada',
        text: 'Este tipo de entrada ya fue agregado',
        confirmButtonText: 'Entendido',
      });
      return;
    }

    const tipoNombre = this.getNombreTipoEntrada(tipoId);

    this.tiposEntradaSeleccionados.push({
      tipoEntradaId: tipoId,
      nombre: tipoNombre,
      precio: this.precioEntrada,
      disponibilidad: this.disponibilidadEntrada,
    });

    this.tipoEntradaSeleccionado = '';
    this.precioEntrada = null;
    this.disponibilidadEntrada = null;

    console.log('Entradas configuradas:', this.tiposEntradaSeleccionados);
  }

  eliminarTipoEntrada(index: number): void {
    this.tiposEntradaSeleccionados.splice(index, 1);
  }

  getNombreTipoEntrada(id: number): string {
    const tipo = this.tiposEntrada.find((t) => t.id === id);
    return tipo ? tipo.entrada : 'Desconocido';
  }

  tiposEntradaDisponibles(): any[] {
    return this.tiposEntrada.filter(
      (tipo) =>
        !this.tiposEntradaSeleccionados.some((e) => e.tipoEntradaId === tipo.id)
    );
  }

  calcularTotalTickets(): number {
    return this.tiposEntradaSeleccionados.reduce((total, entrada) => {
      return total + (entrada.disponibilidad || 0);
    }, 0);
  }

  // =========================
  // Imagen
  // =========================

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const maxSize = 5 * 1024 * 1024;
      if (file.size > maxSize) {
        Swal.fire({
          icon: 'warning',
          title: 'Archivo muy grande',
          text: 'La imagen no debe superar los 5MB',
          confirmButtonText: 'Entendido',
        });
        return;
      }

      this.selectedFile = file;
      this.eventForm.patchValue({ imagen: file.name });
      this.eventForm.get('imagen')?.updateValueAndValidity();

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage(): void {
    this.selectedFile = null;
    this.imagePreview = this.imagenActual;
    this.eventForm.patchValue({ imagen: '' });
    this.eventForm.get('imagen')?.updateValueAndValidity();
  }

  // =========================
  // Submit / confirmación / PUT
  // =========================

  get f() {
    return this.eventForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.artistasSeleccionados.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Artistas requeridos',
        text: 'Debe agregar al menos un artista al evento',
        confirmButtonText: 'Entendido',
      });
      return;
    }

    if (this.tiposEntradaSeleccionados.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Entradas requeridas',
        text: 'Debe agregar al menos un tipo de entrada',
        confirmButtonText: 'Entendido',
      });
      return;
    }

    if (this.eventForm.invalid) {
      Swal.fire({
        icon: 'warning',
        title: 'Formulario incompleto',
        text: 'Por favor, completa todos los campos requeridos',
        confirmButtonText: 'Entendido',
      });
      return;
    }

    const nombreEvento = this.eventForm.get('nombreEvento')?.value;
    const fecha = this.eventForm.get('fecha')?.value;
    const hora = this.eventForm.get('hora')?.value;

    const establecimiento = this.establecimientos.find(
      (e) => e.id == this.eventForm.get('establecimientoId')?.value
    );
    const totalTickets = this.calcularTotalTickets();

    Swal.fire({
      title: '¿Actualizar evento?',
      html: `
        <div style="text-align: left; padding: 10px;">
          <p><strong>Evento:</strong> ${nombreEvento}</p>
          <p><strong>Lugar:</strong> ${
            establecimiento?.establecimiento || 'N/A'
          }</p>
          <p><strong>Fecha:</strong> ${this.formatearFecha(fecha)}</p>
          <p><strong>Hora:</strong> ${hora}</p>
          <p><strong>Artistas:</strong> ${this.artistasSeleccionados.length}</p>
          <p><strong>Tipos de entrada:</strong> ${
            this.tiposEntradaSeleccionados.length
          }</p>
          <p><strong>Total de tickets:</strong> ${totalTickets}</p>
        </div>
      `,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '✅ Sí, actualizar',
      cancelButtonText: '❌ Cancelar',
      reverseButtons: true,
      customClass: {
        popup: 'swal-wide',
      },
    }).then((result) => {
      if (result.isConfirmed) {
        this.actualizarEvento();
      }
    });
  }

  private formatearFecha(fecha: string): string {
    if (!fecha) return 'N/A';
    const fechaObj = new Date(fecha + 'T00:00:00');
    return fechaObj.toLocaleDateString('es-AR', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  private actualizarEvento(): void {
    Swal.fire({
      title: 'Actualizando evento...',
      html: 'Por favor espera un momento',
      allowOutsideClick: false,
      allowEscapeKey: false,
      didOpen: () => {
        Swal.showLoading();
      },
    });

    // armamos las entradasDetalle para el backend
    const entradasDetalle = this.tiposEntradaSeleccionados.map((entrada) => ({
      tipo: entrada.tipoEntradaId,
      precio: entrada.precio,
      disponibilidad: entrada.disponibilidad,
    }));

    const dto = {
      evento: this.eventForm.get('nombreEvento')?.value,
      descripcion: this.eventForm.get('descripcion')?.value,
      artistaId: this.artistasSeleccionados, // <- IDs listos
      productorId: this.eventForm.get('productorId')?.value,
      entradasDetalle: entradasDetalle,
      establecimientoId: this.eventForm.get('establecimientoId')?.value,
      clasificacionId: this.eventForm.get('clasificacionId')?.value,
      fecha: this.eventForm.get('fecha')?.value,
      hora: this.eventForm.get('hora')?.value,
      activo: this.eventForm.get('activo')?.value,
    };

    const formData = new FormData();
    formData.append('dto', JSON.stringify(dto));

    if (this.selectedFile) {
      formData.append('imagen', this.selectedFile, this.selectedFile.name);
    }

    console.log('DTO actualización enviado:', dto);

    this.eventService.actualizarEvento(this.eventoId, formData).subscribe({
      next: (response) => {
        console.log('Evento actualizado exitosamente:', response);
        Swal.fire({
          icon: 'success',
          title: '¡Evento actualizado!',
          html: `
            <p>El evento <strong>${dto.evento}</strong> ha sido actualizado exitosamente.</p>
            <p class="text-muted small">Los cambios ya están disponibles en el sistema.</p>
          `,
          confirmButtonText: 'Aceptar',
          timer: 3000,
          timerProgressBar: true,
        }).then(() => {
          this.volver();
        });
      },
      error: (error) => {
        console.error('Error al actualizar evento:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error al actualizar evento',
          html: `
            <p>${
              error.error?.message ||
              'Ocurrió un error al intentar actualizar el evento'
            }</p>
            <p class="text-muted small">Por favor, verifica los datos e intenta nuevamente.</p>
          `,
          confirmButtonText: 'Entendido',
          confirmButtonColor: '#d33',
        });
      },
    });
  }

  resetForm(): void {
    this.submitted = false;
    // volvemos a pedir todo del backend para refrescar
    this.cargarTodo();

    this.artistaSeleccionado = '';
    this.tipoEntradaSeleccionado = '';
    this.precioEntrada = null;
    this.disponibilidadEntrada = null;
    this.selectedFile = null;
  }

  volver(): void {
    this.router.navigate(['/eventos']);
  }
}
