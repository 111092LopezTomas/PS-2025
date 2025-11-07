import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  templateUrl: './user-edit.component.html',
  imports: [CommonModule, FormsModule],
})
export class UserEditComponent implements OnInit {
  // modelo principal del formulario
  model = {
    username: '',
    email: '',
    password: '', // opcional
  };

  // campos extendidos
  nombre = '';
  representante = '';
  telefono_representante = '';
  red_social = '';
  idGenero: number | null = null;
  generos: any[] = [];

  // imagen
  selectedFile: File | null = null;
  imagenUrl: string | null = null;

  // estado
  error = '';
  success = '';
  loading = true;

  private userId!: number;
  usuarioRol: string | null = null; // 👈 rol que viene del back

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // ahora tomamos el id del localStorage (como vimos antes)
    const idFromStorage = this.authService.getUserId();
    if (!idFromStorage) {
      this.error = 'No se pudo obtener el usuario actual.';
      this.loading = false;
      return;
    }

    this.userId = Number(idFromStorage);

    // cargar datos del usuario logueado
    this.usuarioService.getUsuarioById(this.userId).subscribe({
      next: (u: any) => {
        // Asignar valores al modelo
        this.model.username = u.username || '';
        this.model.email = u.email || '';

        // guardar rol que vino del back
        this.usuarioRol = u.rol || null;

        // datos extra según artista/productor
        this.nombre = u.nombre || '';
        this.representante = u.representante || '';
        this.telefono_representante = u.telefono_representante || '';
        this.red_social = u.red_social || '';
        this.idGenero = u.idGenero || u.genero?.id || null;

        // si tu back sirve imagen
        if (u.id) {
          this.imagenUrl = `http://localhost:8080/auth/${u.id}/imagen`;
        }

        // cargar géneros solo si el usuario ES artista
        if (this.esArtista()) {
          this.usuarioService.getGeneros().subscribe({
            next: (gs) => (this.generos = gs),
          });
        }

        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudieron cargar los datos del usuario';
        this.loading = false;
      },
    });
  }

  // 👇 ahora no dependemos del AuthService para saber el rol
  esArtista(): boolean {
    return this.usuarioRol === 'ROL_ARTISTA';
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    this.selectedFile = file;
  }

  onSubmit() {
    this.error = '';
    this.success = '';

    const formData = new FormData();
    formData.append('username', this.model.username);
    formData.append('email', this.model.email);

    if (this.model.password && this.model.password.trim() !== '') {
      formData.append('password', this.model.password);
    }

    formData.append('nombre', this.nombre);
    formData.append('representante', this.representante);
    formData.append('telefono_representante', this.telefono_representante);
    formData.append('red_social', this.red_social);

    if (this.idGenero !== null) {
      formData.append('idGenero', this.idGenero.toString());
    }

    if (this.selectedFile) {
      formData.append('imagen', this.selectedFile);
    }

    this.usuarioService.updateUsuario(this.userId, formData).subscribe({
      next: () => {
        this.success = 'Datos actualizados correctamente';
        this.model.password = '';
      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudo actualizar el usuario';
      },
    });
  }
}
