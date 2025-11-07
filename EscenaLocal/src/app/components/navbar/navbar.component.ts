import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EventSearchComponent } from '../event-search/event-search.component';
import { AuthService } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, EventSearchComponent],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit, OnDestroy {
  imagenUrl: string = 'assets/img/usuario.png'; // imagen por defecto
  private subs: Subscription[] = [];
  private objectUrlToRevoke: string | null = null;

  constructor(
    private router: Router,
    private usuarioService: UsuarioService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // cargar imagen si ya hay usuario logueado
    this.cargarImagenUsuario();

    // escuchar cambios en el avatar desde AuthService (reactivo)
    this.subs.push(
      this.authService.avatarState$.subscribe((url) => {
        if (url) {
          this.imagenUrl = url;
        } else {
          this.imagenUrl = 'assets/img/usuario.png';
        }
      })
    );

    // escuchar cambios de login/logout
    this.subs.push(
      this.authService.loginState$.subscribe((logged) => {
        if (logged) {
          this.cargarImagenUsuario();
        } else {
          this.imagenUrl = 'assets/img/usuario.png';
        }
      })
    );
  }

  private cargarImagenUsuario(): void {
    const userId = this.authService.getUserId();
    if (!userId) return;

    this.usuarioService.obtenerImagen(userId).subscribe({
      next: (blob) => {
        if (this.objectUrlToRevoke) {
          URL.revokeObjectURL(this.objectUrlToRevoke);
        }
        const objectURL = URL.createObjectURL(blob);
        this.imagenUrl = objectURL;
        this.objectUrlToRevoke = objectURL;

        // También actualizamos el avatar en el AuthService para compartirlo con otros componentes
        this.authService.setAvatar(objectURL);
      },
      error: () => {
        this.imagenUrl = 'assets/img/usuario.png';
      },
    });
  }

  // Verifica si hay token guardado
  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  // Redirige al login
  irAlLogin(): void {
    this.router.navigate(['/login']);
  }

  // Cierra sesión
  cerrarSesion(): void {
    this.authService.logout();
    this.imagenUrl = 'assets/img/usuario.png';
    this.router.navigate(['/login']);
  }

  verPerfil(): void {
    this.router.navigate(['/perfil']);
  }

  ngOnDestroy(): void {
    if (this.objectUrlToRevoke) {
      URL.revokeObjectURL(this.objectUrlToRevoke);
    }
    this.subs.forEach((s) => s.unsubscribe());
  }
}
