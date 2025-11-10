import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { NotificacionService, Notificacion } from '../../services/notificacion.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  imports: [CommonModule],
})
export class NavbarComponent {
  imagenUrl: string = 'assets/img/usuario.png';
  unreadCount: number = 0;
  notificaciones: Notificacion[] = [];

  constructor(
    private router: Router,
    private usuarioService: UsuarioService,
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
  }

  // Verifica si hay token guardado
  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt') || !!localStorage.getItem('token');
  }

  // Redirige al login
  irAlLogin() {
    this.router.navigate(['/login']);
  }

  // Cierra sesión
  cerrarSesion(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('token');
    localStorage.removeItem('usuarioId');
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
}
