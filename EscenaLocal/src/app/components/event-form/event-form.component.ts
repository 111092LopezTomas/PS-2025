import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-event-form',
  imports: [ReactiveFormsModule, CommonModule],
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

  constructor(
    private fb: FormBuilder,
    private eventService: EventService
  ) {
    this.eventForm = this.fb.group({
      nombreEvento: ['', [Validators.required, Validators.minLength(3)]],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      artistaId: ['', Validators.required],
      productorId: ['', Validators.required],
      tipoEntradaId: ['', Validators.required],
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

  get f() {
    return this.eventForm.controls;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.eventForm.patchValue({ imagen: file.name });
      this.eventForm.get('imagen')?.updateValueAndValidity();

      // Crear preview de la imagen
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
    if (this.eventForm.invalid) {
      return;
    }
    
    const formData = new FormData();
    
    // Crear el objeto DTO con todos los datos del formulario
    const dto = {
      evento: this.eventForm.get('nombreEvento')?.value,
      descripcion: this.eventForm.get('descripcion')?.value,
      artistaId: [this.eventForm.get('artistaId')?.value],
      productorId: this.eventForm.get('productorId')?.value,
      tipoEntradaId: [this.eventForm.get('tipoEntradaId')?.value],
      establecimientoId: this.eventForm.get('establecimientoId')?.value,
      clasificacionId: this.eventForm.get('clasificacionId')?.value,
      fecha: this.eventForm.get('fecha')?.value,
      hora: this.eventForm.get('hora')?.value,
      activo: this.eventForm.get('activo')?.value
    };
    
    // Agregar el DTO como JSON string con el nombre 'dto'
    formData.append('dto', JSON.stringify(dto));
    
    // Agregar la imagen por separado
    if (this.selectedFile) {
      formData.append('imagen', this.selectedFile, this.selectedFile.name);
    }
   
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
  }
}
