import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EventSearchComponent } from '../event-search/event-search.component';
import { AuthService } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';
<<<<<<< HEAD
import { Subscription } from 'rxjs';
=======
import { NotificacionService, Notificacion } from '../../services/notificacion.service';
>>>>>>> backup-previo-pull

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, EventSearchComponent],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
<<<<<<< HEAD
export class NavbarComponent implements OnInit, OnDestroy {
  imagenUrl: string = 'assets/img/usuario.png'; // imagen por defecto
  private subs: Subscription[] = [];
  private objectUrlToRevoke: string | null = null;
=======
export class NavbarComponent {
  imagenUrl: string = 'assets/img/usuario.png';
  unreadCount: number = 0;
  notificaciones: Notificacion[] = [];
>>>>>>> backup-previo-pull

  constructor(
    private router: Router,
    private usuarioService: UsuarioService,
<<<<<<< HEAD
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
=======
    private notificacionService: NotificacionService
  ) {}

  ngOnInit() {
    const usuarioId = Number(localStorage.getItem('usuarioId'));

    // Cargar imagen de usuario
    if (usuarioId) {
      this.usuarioService.obtenerImagen(usuarioId).subscribe({
        next: (blob) => {
          const objectURL = URL.createObjectURL(blob);
          this.imagenUrl = objectURL;
        },
        error: () => {
          this.imagenUrl = 'assets/img/usuario.png';
        },
      });

      // Cargar notificaciones desde el backend
      this.notificacionService.obtenerNotificaciones(usuarioId).subscribe({
        next: (data) => {
          this.notificaciones = data;
          // contar las no leídas
          this.unreadCount = this.notificaciones.filter(n => !n.leido).length;
          console.log('Notificaciones cargadas:', this.notificaciones);
        },
        error: (err) => console.error('Error al cargar notificaciones:', err),
      });
    }
>>>>>>> backup-previo-pull
  }

  // Verifica si hay token guardado
  isLoggedIn(): boolean {
<<<<<<< HEAD
    return this.authService.isLoggedIn();
=======
    return !!localStorage.getItem('jwt') || !!localStorage.getItem('token');
>>>>>>> backup-previo-pull
  }

  // Redirige al login
  irAlLogin(): void {
    this.router.navigate(['/login']);
  }

  // Cierra sesión
  cerrarSesion(): void {
<<<<<<< HEAD
    this.authService.logout();
    this.imagenUrl = 'assets/img/usuario.png';
=======
    localStorage.removeItem('jwt');
    localStorage.removeItem('token');
    localStorage.removeItem('usuarioId');
>>>>>>> backup-previo-pull
    this.router.navigate(['/login']);
    this.notificaciones = [];
    this.unreadCount = 0;
  }

  // Maneja la apertura de una notificación
  abrirNotificacion(n: Notificacion) {
    console.log('Notificación abierta:', n.mensaje);

    // Si la notificación no estaba leída
    if (!n.leido) {
      n.leido = true;
      this.unreadCount = this.notificaciones.filter(notif => !notif.leido).length;

      // Actualizar en el backend
      this.notificacionService.marcarComoLeido(n.id).subscribe({
        next: () => {
          console.log(`Notificación ${n.id} marcada como leída en el backend`);
        },
        error: (err) => {
          console.error('Error al marcar como leído:', err);
          // Si falla, revertir el cambio local
          n.leido = false;
          this.unreadCount = this.notificaciones.filter(notif => !notif.leido).length;
        },
      });
    }
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
