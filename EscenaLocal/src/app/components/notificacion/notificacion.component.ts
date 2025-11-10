import { Component, OnInit } from '@angular/core';
import { NotificacionService, Notificacion } from '../../services/notificacion.service';

@Component({
  selector: 'app-notificacion',
  templateUrl: './notificacion.component.html',
  styleUrls: ['./notificacion.component.css']
})
export class NotificacionComponent implements OnInit {
  notificaciones: Notificacion[] = [];
  showDropdown = false;

  constructor(private notificacionService: NotificacionService) {}

  ngOnInit() {
    const userId = Number(localStorage.getItem('userId')); 
    this.notificacionService.obtenerNotificaciones(userId).subscribe(data => {
      this.notificaciones = data;
    });
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
  }

  abrirNotificacion(n: Notificacion) {
    alert(n.mensaje);
    this.notificacionService.marcarComoLeido(n.id).subscribe(() => {
      n.leido = true;
    });
  }

  get unreadCount() {
    return this.notificaciones.filter(n => !n.leido).length;
  }
}
