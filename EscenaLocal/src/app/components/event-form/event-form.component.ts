import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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

  // ========== PROPIEDADES PARA MÚLTIPLES ARTISTAS Y ENTRADAS ==========
  artistasSeleccionados: number[] = [];
  artistaSeleccionado: string = '';
  tiposEntradaSeleccionados: any[] = []; // Array de objetos {tipoEntradaId, nombre, precio, disponibilidad}
  tipoEntradaSeleccionado: string = '';
  precioEntrada: number | null = null;
  disponibilidadEntrada: number | null = null;

  constructor(
    private fb: FormBuilder,
    private eventService: EventService
  ) {
    this.eventForm = this.fb.group({
      nombreEvento: ['', [Validators.required, Validators.minLength(3)]],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      productorId: ['', Validators.required],
      establecimientoId: ['', Validators.required],
      clasificacionId: ['', Validators.required],
      fecha: ['', Validators.required],
      hora: ['', Validators.required],
      imagen: ['', Validators.required],
      activo: [true]
    });
  }

  ngOnInit(): void {
    this.cargarArtistas();
    this.cargarProductores();
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
      }
    });
  }

  cargarProductores(): void {
    this.eventService.getProductores().subscribe({
      next: (data) => {
        this.productores = data;
      },
      error: (error) => {
        console.error('Error al cargar productores:', error);
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
      }
    });
  }

  // ========== MÉTODOS PARA ARTISTAS MÚLTIPLES ==========
  
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

  // ========== MÉTODOS PARA TIPOS DE ENTRADA MÚLTIPLES ==========
  
  agregarTipoEntrada(): void {
    if (!this.tipoEntradaSeleccionado || !this.precioEntrada || !this.disponibilidadEntrada) {
      return;
    }

    const tipoId = Number(this.tipoEntradaSeleccionado);
    
    // Verificar que no esté ya agregado
    if (this.tiposEntradaSeleccionados.some(e => e.tipoEntradaId === tipoId)) {
      alert('Este tipo de entrada ya fue agregado');
      return;
    }

    // Buscar el nombre del tipo de entrada
    const tipoNombre = this.getNombreTipoEntrada(tipoId);

    // Agregar el objeto completo
    this.tiposEntradaSeleccionados.push({
      tipoEntradaId: tipoId,
      nombre: tipoNombre,
      precio: this.precioEntrada,
      disponibilidad: this.disponibilidadEntrada
    });

    // Limpiar campos
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

  // ========== MÉTODOS ORIGINALES ==========

  get f() {
    return this.eventForm.controls;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
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

    // Validar que haya al menos un artista
    if (this.artistasSeleccionados.length === 0) {
      alert('Debe agregar al menos un artista');
      return;
    }

    // Validar que haya al menos un tipo de entrada
    if (this.tiposEntradaSeleccionados.length === 0) {
      alert('Debe agregar al menos un tipo de entrada');
      return;
    }

    if (this.eventForm.invalid) {
      return;
    }
    
    const formData = new FormData();
    
    // Mapear las entradas al formato requerido por el backend
    const entradasDetalle = this.tiposEntradaSeleccionados.map(entrada => ({
      tipo: entrada.tipoEntradaId,
      precio: entrada.precio,
      disponibilidad: entrada.disponibilidad
    }));
    
    // Crear el objeto DTO
    const dto = {
      evento: this.eventForm.get('nombreEvento')?.value,
      descripcion: this.eventForm.get('descripcion')?.value,
      artistaId: this.artistasSeleccionados,
      productorId: this.eventForm.get('productorId')?.value,
      entradasDetalle: entradasDetalle, // Array de objetos con precio y disponibilidad
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
    console.log('Artistas seleccionados:', this.artistasSeleccionados);
    console.log('Entradas con precio y disponibilidad:', entradasDetalle);

    this.eventService.crearEvento(formData).subscribe({
      next: (response) => {
        console.log('Evento creado exitosamente:', response);
        alert('Evento creado exitosamente');
        this.resetForm();
      },
      error: (error) => {
        console.error('Error al crear evento:', error);
        alert('Error al crear el evento');
      }
    });
  }

  resetForm(): void {
    this.submitted = false;
    this.eventForm.reset();
    this.selectedFile = null;
    this.imagePreview = null;
    this.artistasSeleccionados = [];
    this.artistaSeleccionado = '';
    this.tiposEntradaSeleccionados = [];
    this.tipoEntradaSeleccionado = '';
    this.precioEntrada = null;
    this.disponibilidadEntrada = null;
  }
}