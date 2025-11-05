import { Component, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService, AuthRequest } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements AfterViewInit {

  model: AuthRequest = { username: '', password: '', rol: 'ROL_USUARIO' };
  error: string | null = null;
  @ViewChild('usernameInput') usernameInput!: ElementRef<HTMLInputElement>;

  constructor(
    private authService: AuthService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  ngAfterViewInit(): void {
    // Foco al cargar el componente
    this.focusInput();

    // También al volver al login
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.focusInput());
  }

  private focusInput(): void {
    setTimeout(() => {
      this.usernameInput?.nativeElement.focus();
    }, 0);
  }

  onSubmit() {
    this.error = null;

    this.authService.login(this.model).subscribe({
      next: (res) => {
        localStorage.setItem('jwt', res.token);
        localStorage.setItem('usuarioId', res.userId.toString());

        // 🔹 Pedir la imagen apenas se loguea
        this.usuarioService.obtenerImagen(res.userId).subscribe({
          next: (blob) => {
            const objectURL = URL.createObjectURL(blob);
            localStorage.setItem('imagenUsuario', objectURL);
          },
          error: () => {
            localStorage.removeItem('imagenUsuario');
          },
          complete: () => {
            // Redirigir al home al finalizar la carga de imagen
            this.router.navigate(['/']);
          }
        });
      },
      error: (err) => {
        this.error = err?.error?.message || 'Error en login';
      }
    });
  }

  irAlLogin(event: Event) {
    event.preventDefault(); // evita que el # recargue la página
    this.router.navigate(['/login']);
  }

  irARegistrar() {
    this.router.navigate(['/login/nuevo']);
  }
}
