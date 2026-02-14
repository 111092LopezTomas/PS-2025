import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';
import { AuthService, AuthRequest, RegisterRequest } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css'],
})
export class LoginFormComponent {

  model: AuthRequest = { username: '', password: '', email: '', rol: 'ROL_USUARIO' };
  selectedFile: File | null = null;
  modoRegistro: boolean = true;
  error: string | null = null;

  constructor(
    private authService: AuthService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  alternarModo() {
    this.modoRegistro = !this.modoRegistro;
    this.error = null;
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit() {
  if (this.modoRegistro) {
    const data: RegisterRequest = {
      username: this.model.username,
      password: this.model.password,
      email: this.model.email!,
      imagen: this.selectedFile || undefined // 👈 incluimos la imagen directamente

    };

    this.authService.register(data, 1).subscribe({
      next: (res) => {
        
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
        this.router.navigate(['/home']);
      },
      error: () => {
        this.error = 'Usuario o contraseña incorrectos';
      }
    });
  }
}


  irAlLogin() {    
    this.router.navigate(['/login']);
  }
}
