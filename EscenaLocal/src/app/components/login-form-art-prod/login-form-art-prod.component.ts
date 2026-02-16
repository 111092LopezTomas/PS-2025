import { Component, OnInit } from '@angular/core';
import {
  AuthRequest,
  AuthService,
  RegisterRequest,
  Rol
} from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Genero {
  id: number;
  genero: string;
}

@Component({
  selector: 'app-login-form-art-prod',
  imports: [CommonModule, FormsModule],
  templateUrl: './login-form-art-prod.component.html',
  styleUrl: './login-form-art-prod.component.css'
})
export class LoginFormArtProdComponent implements OnInit {
  // login / registro básico
  model: AuthRequest = { username: '', password: '', email: '', rol: '' };
  selectedRolId: number | null = null;
  selectedFile: File | null = null;
  modoRegistro: boolean = true;
  error: string | null = null;
  roles: Rol[] = [];

  // 👇 datos comunes a Artista y Productor
  nombre: string = '';
  representante: string = '';
  telefono_representante: string = '';
  red_social: string = '';

  // 👇 solo para artista
  generos: Genero[] = [];
  idGenero: number | null = null;

  constructor(
    private authService: AuthService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarRoles();
    this.cargarGeneros(); // para el caso de artista
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) this.selectedFile = file;
  }

  
onSubmit() {
  // Este componente se usa para registrar artistas/productores
  this.modoRegistro = true;

  if (!this.selectedRolId) {
    this.error = 'Seleccione un rol.';
    return;
  }

  const selId = Number(this.selectedRolId);
  const rolSeleccionado = this.roles.find(r => r.id === selId);
  const esArtista = rolSeleccionado?.rol === 'ROL_ARTISTA';
  const esProductor = rolSeleccionado?.rol === 'ROL_PRODUCTOR';

  // ---- Registro ARTISTA / PRODUCTOR ----
  if (esArtista || esProductor) {
    const payload: any = {
      username: this.model.username,
      password: this.model.password,
      email: this.model.email,
      tipo: esArtista ? 'ARTISTA' : 'PRODUCTOR',
      nombre: this.nombre,
      representante: this.representante,
      telefono_representante: this.telefono_representante,
      red_social: this.red_social,
    };

    if (esArtista) {
      payload.idGenero = this.idGenero;
    }

    this.authService.registerArtProd(payload).subscribe({
     next: (res) => {
  const userId = Number(res.userId);
  const token = res.token;

  // si hay imagen, subir primero; después "loguear" con el AuthService
  if (this.selectedFile) {
    this.usuarioService.subirImagen(userId, this.selectedFile).subscribe({
      next: () => {
        console.log('Imagen subida correctamente');
        this.authService.setSession(token, userId);
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error('Error al subir imagen:', err);
        this.authService.setSession(token, userId);
        this.router.navigate(['/home']);
      }
    });
  } else {
    this.authService.setSession(token, userId);
    this.router.navigate(['/home']);
  }
},
      error: (err) => {
        console.error('Error al registrar artista/productor:', err);
        this.error = 'Error al registrar artista/productor';
      }
    });

    return; // salimos, ya hicimos el registro completo
  }

  // ---- Registro normal (usuario común) ----
  const data: RegisterRequest = {
    username: this.model.username,
    password: this.model.password,
    email: this.model.email!,
    imagen: this.selectedFile || undefined
  };

  this.authService.register(data, this.selectedRolId).subscribe({
    next: (res) => {
      localStorage.setItem('jwt', res.token);
      localStorage.setItem('usuarioId', res.userId.toString());
      this.router.navigate(['/home']);
    },
    error: (err) => {
      console.error('Error al registrarse:', err);
      this.error = 'Error al registrarse';
    }
  });
}


  cargarRoles() {
    this.authService.getRoles().subscribe({
      next: (data) => (this.roles = data),
      error: (err) => console.error('Error al cargar roles:', err)
    });
  }

  

  cargarGeneros() {
  this.usuarioService.getGeneros().subscribe({
    next: (data) => {
      console.log('Géneros cargados:', data); // 👈 agregá esto
      this.generos = data;
    },
    error: (err) => console.error('Error al cargar géneros:', err)
  });
}

  irAlLogin() {
    this.router.navigate(['/login/art-prod']);
  }

  esArtista(): boolean {
  if (!this.roles || this.roles.length === 0) return false;
  if (this.selectedRolId == null) return false;

  const selId = Number(this.selectedRolId); // 👈 forzamos a número
  const rolSel = this.roles.find(r => r.id === selId);
  const nombreRol = rolSel?.rol || '';

  return nombreRol === 'ROL_ARTISTA' || nombreRol === 'ARTISTA';
}

}
