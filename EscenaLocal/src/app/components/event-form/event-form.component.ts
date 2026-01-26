import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router'; // 👈 agregado

@Component({
  selector: 'app-event-form',
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './event-form.component.html',
  styleUrl: './event-form.component.css'
})
export class EventFormComponent {
  eventForm: FormGroup;
  artistas: any[] = [];
  productores: any[] = [];
  tiposEntrada: any[] = [];
  establecimientos: any[] = [];
  clasificaciones: any[] = [];
  isLoading = false;
  submitted = false;
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  artistasSeleccionados: number[] = [];
  artistaSeleccionado: string = '';
  tiposEntradaSeleccionados: any[] = [];
  tipoEntradaSeleccionado: string = '';
  precioEntrada: number | null = null;
  disponibilidadEntrada: number | null = null;

  // id de PRODUCTOR que vamos a enviar al back (viene desde el perfil)
  productorIdFijado: number | null = null;

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private router: Router,
    private route: ActivatedRoute     // 👈 inyectamos ActivatedRoute
  ) {
    this.eventForm = this.fb.group({
      nombreEvento: ['', [Validators.required, Validators.minLength(3)]],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      productorId: ['', Validators.required],          // este es el productorId REAL
      establecimientoId: ['', Validators.required],
      clasificacionId: ['', Validators.required],
      fecha: ['', Validators.required],
      hora: ['', Validators.required],
      imagen: ['', Validators.required],
      activo: [true]
    });
  }

  ngOnInit(): void {
    // 👇 leemos productorId desde la URL (?productorId=5)
    this.route.queryParamMap.subscribe(params => {
      const prodIdParam = params.get('productorId');
      if (prodIdParam) {
        this.productorIdFijado = +prodIdParam;
        this.eventForm.get('productorId')?.setValue(this.productorIdFijado);
        console.log('[EventForm] productorIdFijado desde URL =', this.productorIdFijado);
      } else {
        console.warn('[EventForm] No llegó productorId en query params');
      }
    });

    this.cargarArtistas();
    this.cargarProductores();   // opcional, solo informativo
    this.cargarTiposEntrada();
    this.cargarEstablecimientos();
    this.cargarClasificaciones();
  }

  cargarArtistas(): void {
    this.isLoading = true;
    this.eventService.getArtistas().subscribe({
      next: (data) => {
        this.artistas = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar artistas:', error);
        this.isLoading = false;
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar los artistas',
          confirmButtonText: 'Entendido'
        });
      }
    });
  }

  cargarProductores(): void {
    this.eventService.getProductores().subscribe({
      next: (data) => {
        this.productores = data;
        console.log('[EventForm] productores cargados (info):', this.productores);
      },
      error: (error) => {
        console.error('Error al cargar productores:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar los productores'
        });
      }
    });
  }

  cargarTiposEntrada(): void {
    this.eventService.getTiposEntrada().subscribe({
      next: (data) => {
        this.tiposEntrada = data;
      },
      error: (error) => {
        console.error('Error al cargar tipos de entrada:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar los tipos de entrada'
        });
      }
    });
  }

  cargarEstablecimientos(): void {
    this.eventService.getEstablecimientos().subscribe({
      next: (data) => {
        this.establecimientos = data;
        console.log('Establecimientos cargados:', data);
      },
      error: (error) => {
        console.error('Error al cargar establecimientos:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar los establecimientos'
        });
      }
    });
  }

  cargarClasificaciones(): void {
    this.eventService.getClasificaciones().subscribe({
      next: (data) => {
        this.clasificaciones = data;
      },
      error: (error) => {
        console.error('Error al cargar clasificaciones:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar las clasificaciones'
        });
      }
    });
  }
  
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
    const artista = this.artistas.find(a => a.id === id);
    return artista ? artista.nombre : 'Desconocido';
  }

  artistasDisponibles(): any[] {
    return this.artistas.filter(artista => 
      !this.artistasSeleccionados.includes(artista.id)
    );
  }
  
  agregarTipoEntrada(): void {
    if (!this.tipoEntradaSeleccionado || !this.precioEntrada || !this.disponibilidadEntrada) {
      Swal.fire({
        icon: 'warning',
        title: 'Campos incompletos',
        text: 'Debes completar el tipo de entrada, precio y disponibilidad',
        confirmButtonText: 'Entendido'
      });
      return;
    }

    const tipoId = Number(this.tipoEntradaSeleccionado);
    
    if (this.tiposEntradaSeleccionados.some(e => e.tipoEntradaId === tipoId)) {
      Swal.fire({
        icon: 'info',
        title: 'Entrada duplicada',
        text: 'Este tipo de entrada ya fue agregado',
        confirmButtonText: 'Entendido'
      });
      return;
    }

    const tipoNombre = this.getNombreTipoEntrada(tipoId);

    this.tiposEntradaSeleccionados.push({
      tipoEntradaId: tipoId,
      nombre: tipoNombre,
      precio: this.precioEntrada,
      disponibilidad: this.disponibilidadEntrada
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
    const tipo = this.tiposEntrada.find(t => t.id === id);
    return tipo ? tipo.entrada : 'Desconocido';
  }

  tiposEntradaDisponibles(): any[] {
    return this.tiposEntrada.filter(tipo => 
      !this.tiposEntradaSeleccionados.some(e => e.tipoEntradaId === tipo.id)
    );
  }

  calcularTotalTickets(): number {
    return this.tiposEntradaSeleccionados.reduce((total, entrada) => {
      return total + (entrada.disponibilidad || 0);
    }, 0);
  }

  get f() {
    return this.eventForm.controls;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const maxSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxSize) {
        Swal.fire({
          icon: 'warning',
          title: 'Archivo muy grande',
          text: 'La imagen no debe superar los 5MB',
          confirmButtonText: 'Entendido'
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
    this.imagePreview = null;
    this.eventForm.patchValue({ imagen: '' });
    this.eventForm.get('imagen')?.updateValueAndValidity();
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.artistasSeleccionados.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Artistas requeridos',
        text: 'Debe agregar al menos un artista al evento',
        confirmButtonText: 'Entendido'
      });
      return;
    }

    if (this.tiposEntradaSeleccionados.length === 0) {
      Swal.fire({
        icon: 'warning',
        title: 'Entradas requeridas',
        text: 'Debe agregar al menos un tipo de entrada',
        confirmButtonText: 'Entendido'
      });
      return;
    }

    if (this.eventForm.invalid) {
      Swal.fire({
        icon: 'warning',
        title: 'Formulario incompleto',
        text: 'Por favor, completa todos los campos requeridos',
        confirmButtonText: 'Entendido'
      });
      return;
    }

    const productorIdValor = this.eventForm.get('productorId')?.value;
    console.log('[EventForm] productorId que se enviará =', productorIdValor);

    const nombreEvento = this.eventForm.get('nombreEvento')?.value;
    const fecha = this.eventForm.get('fecha')?.value;
    const hora = this.eventForm.get('hora')?.value;
    const establecimiento = this.establecimientos.find(e => e.id == this.eventForm.get('establecimientoId')?.value);
    const totalTickets = this.calcularTotalTickets();

    Swal.fire({
      title: '¿Crear nuevo evento?',
      html: `
        <div style="text-align: left; padding: 10px;">
          <p><strong>Evento:</strong> ${nombreEvento}</p>
          <p><strong>Lugar:</strong> ${establecimiento?.establecimiento || 'N/A'}</p>
          <p><strong>Fecha:</strong> ${this.formatearFecha(fecha)}</p>
          <p><strong>Hora:</strong> ${hora}</p>
          <p><strong>Artistas:</strong> ${this.artistasSeleccionados.length}</p>
          <p><strong>Tipos de entrada:</strong> ${this.tiposEntradaSeleccionados.length}</p>
          <p><strong>Total de tickets:</strong> ${totalTickets}</p>
          <p><strong>Productor ID (evento):</strong> ${productorIdValor}</p>
        </div>
      `,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '✅ Sí, crear evento',
      cancelButtonText: '❌ Cancelar',
      reverseButtons: true,
      customClass: {
        popup: 'swal-wide'
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.crearEvento();
        this.volver();
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
      day: 'numeric' 
    });
  }

  private crearEvento(): void {
    Swal.fire({
      title: 'Creando evento...',
      html: 'Por favor espera un momento',
      allowOutsideClick: false,
      allowEscapeKey: false,
      didOpen: () => {
        Swal.showLoading();
      }
    });

    const formData = new FormData();
    
    const entradasDetalle = this.tiposEntradaSeleccionados.map(entrada => ({
      tipo: entrada.tipoEntradaId,
      precio: entrada.precio,
      disponibilidad: entrada.disponibilidad
    }));
    
    const dto = {
      evento: this.eventForm.get('nombreEvento')?.value,
      descripcion: this.eventForm.get('descripcion')?.value,
      artistaId: this.artistasSeleccionados,
      productorId: this.eventForm.get('productorId')?.value,   // 👈 id de PRODUCTOR
      entradasDetalle: entradasDetalle,
      establecimientoId: this.eventForm.get('establecimientoId')?.value,
      clasificacionId: this.eventForm.get('clasificacionId')?.value,
      fecha: this.eventForm.get('fecha')?.value,
      hora: this.eventForm.get('hora')?.value,
      activo: this.eventForm.get('activo')?.value
    };
    
    formData.append('dto', JSON.stringify(dto));
    
    if (this.selectedFile) {
      formData.append('imagen', this.selectedFile, this.selectedFile.name);
    }
   
    console.log('DTO enviado:', dto);

    this.eventService.crearEvento(formData).subscribe({
      next: (response) => {
        console.log('Evento creado exitosamente:', response);
        Swal.fire({
          icon: 'success',
          title: '¡Evento creado!',
          html: `
            <p>El evento <strong>${dto.evento}</strong> ha sido creado exitosamente.</p>
            <p class="text-muted small">Ya está disponible en el sistema.</p>
          `,
          confirmButtonText: 'Aceptar',
          timer: 3000,
          timerProgressBar: true
        }).then(() => {
          this.resetForm();
        });
      },
      error: (error) => {
        console.error('Error al crear evento:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error al crear evento',
          html: `
            <p>${error.error?.message || 'Ocurrió un error al intentar crear el evento'}</p>
            <p class="text-muted small">Por favor, verifica los datos e intenta nuevamente.</p>
          `,
          confirmButtonText: 'Entendido',
          confirmButtonColor: '#d33'
        });
      }
    });
  }

  resetForm(): void {
    this.submitted = false;

    this.eventForm.reset({
      activo: true,
      productorId: this.productorIdFijado   // dejamos fijo el productorId también al resetear
    });

    this.selectedFile = null;
    this.imagePreview = null;
    this.artistasSeleccionados = [];
    this.artistaSeleccionado = '';
    this.tiposEntradaSeleccionados = [];
    this.tipoEntradaSeleccionado = '';
    this.precioEntrada = null;
    this.disponibilidadEntrada = null;
  }

  volver() {
    this.router.navigate(['/eventos']);
  }
}
