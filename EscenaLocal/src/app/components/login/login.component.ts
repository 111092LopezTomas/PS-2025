import { Component, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService, AuthRequest } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
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
    this.focusInput();
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
    console.log('ENTRÉ AL ONSUBMIT');
    this.error = null;

    this.authService.login(this.model).subscribe({
      next: (res) => {
        localStorage.setItem('usuarioId', res.userId.toString());

        this.usuarioService.obtenerImagen(res.userId).subscribe({
          next: (blob) => {
            const objectURL = URL.createObjectURL(blob);
            localStorage.setItem('imagenUsuario', objectURL);
          },
          error: () => {
            localStorage.removeItem('imagenUsuario');
          },
          complete: () => {
            this.router.navigate(['/']);
          }
        });
      },
      error: (err) => {
        this.error = err?.error?.message || 'Error en login';
      }
    });
  }

  irARegistrar() {
    this.router.navigate(['/login/nuevo']);
  }
}
