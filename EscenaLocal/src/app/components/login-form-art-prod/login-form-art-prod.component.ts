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

@Component({
  selector: 'app-login-form-art-prod',
  imports: [CommonModule, FormsModule],
  templateUrl: './login-form-art-prod.component.html',
  styleUrl: './login-form-art-prod.component.css'
})
export class LoginFormArtProdComponent implements OnInit {
  model: AuthRequest = { username: '', password: '', email: '', rol: '' };
  selectedRolId: number | null = null; // 👈 ahora es numérico
  selectedFile: File | null = null;
  modoRegistro: boolean = true;
  error: string | null = null;
  roles: Rol[] = [];

  constructor(
    private authService: AuthService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarRoles();
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) this.selectedFile = file;
  }

  onSubmit() {
    if (this.modoRegistro) {
      if (!this.selectedRolId) {
        this.error = 'Seleccione un rol.';
        return;
      }

      const data: RegisterRequest = {
        username: this.model.username,
        password: this.model.password,
        email: this.model.email!,
        imagen: this.selectedFile || undefined
      };

      // ✅ ahora mandamos el ID numérico del rol
      this.authService.register(data, this.selectedRolId).subscribe({
        next: (res) => {
          localStorage.setItem('jwt', res.token);
          localStorage.setItem('usuarioId', res.userId.toString());
          this.router.navigate(['/home']);
        },
        error: () => {
          this.error = 'Error al registrarse';
        }
      });
    } else {
      this.authService.login(this.model).subscribe({
        next: (res) => {
          localStorage.setItem('jwt', res.token);
          this.router.navigate(['/home']);
        },
        error: () => {
          this.error = 'Usuario o contraseña incorrectos';
        }
      });
    }
  }

  cargarRoles() {
    this.authService.getRoles().subscribe({
      next: (data) => (this.roles = data),
      error: (err) => console.error('Error al cargar roles:', err)
    });
  }

  irAlLogin() {    
    this.router.navigate(['/login']);
  }

}
