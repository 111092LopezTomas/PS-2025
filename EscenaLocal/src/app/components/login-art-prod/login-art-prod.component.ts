import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  AfterViewInit
} from '@angular/core';
import { AuthRequest, AuthService, Rol } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-art-prod',
  imports: [FormsModule, CommonModule],
  templateUrl: './login-art-prod.component.html',
  styleUrl: './login-art-prod.component.css'
})
export class LoginArtProdComponent implements OnInit, AfterViewInit {
  model: AuthRequest = { username: '', password: '', rol: '' };
  error: string | null = null;
  @ViewChild('usernameInput') usernameInput!: ElementRef<HTMLInputElement>;
  roles: Rol[] = [];

  constructor(
    private authService: AuthService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarRoles();
  }

  ngAfterViewInit(): void {
    this.focusInput();

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => this.focusInput());
  }

  private focusInput(): void {
    setTimeout(() => {
      this.usernameInput?.nativeElement.focus();
    }, 0);
  }

  /* onSubmit() {
    this.error = null;
    console.log('Rol seleccionado:', this.model.rol);

    // pequeña validación por las dudas
    if (!this.model.rol) {
      this.error = 'Seleccione un rol.';
      return;
    }

     console.log('voy a loguear con:', this.model);

    this.authService.login(this.model).subscribe({
      next: (res) => {
        // 1) guardo el token en el servicio
        this.authService.saveToken(res.token);

        // 2) comparo el rol elegido vs el rol real del token
        const ok = this.authService.validateSelectedRole(this.model.rol);
        if (!ok) {
          // si no coincide, limpio token y aviso
          this.authService.clearToken();
          this.error =
            'El rol seleccionado no coincide con el rol asignado al usuario.';
          return;
        }

        // 3) si coincide, guardo el id de usuario (esto sí en localStorage como ya lo hacías)
        localStorage.setItem('usuarioId', res.userId.toString());

        // 4) pido la imagen y recién ahí navego
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
  } */

    onSubmit() {
  this.error = null;

  if (!this.model.rol) {
    this.error = 'Seleccione un rol.';
    return;
  }

  this.authService.login(this.model).subscribe({
    next: (res) => {
      // 1. guardo token e id
      this.authService.saveToken(res.token);
      localStorage.setItem('usuarioId', res.userId.toString());

      // 2. VALIDAR rol del token vs rol elegido
      const ok = this.authService.validateSelectedRole(this.model.rol);
      console.log('rol coincide?', ok);

      if (!ok) {
        // si NO coincide, corto todo acá
        this.authService.clearToken();
        localStorage.removeItem('usuarioId');
        this.error =
          'El rol seleccionado no coincide con el rol asignado al usuario.';
        return; // 👈 este return es clave
      }

      // 3. SOLO si coincide, pido la imagen y navego
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
      console.error('Error en login:', err);
      this.error = err?.error?.message || 'Error en login';
    }
  });
}


  irAlLogin(event: Event) {
    event.preventDefault();
    this.router.navigate(['/login']);
  }

  irARegistrar() {
    this.router.navigate(['/login/art-prod/nuevo']);
  }

  cargarRoles() {
    this.authService.getRoles().subscribe({
      next: (data) => {
        this.roles = data;
      },
      error: (error) => {
        console.error('Error al cargar roles:', error);
      }
    });
  }
}
