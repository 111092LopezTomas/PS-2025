import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { EventSearchComponent } from '../event-search/event-search.component';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  imports: [CommonModule, EventSearchComponent],
})
export class NavbarComponent {
  imagenUrl: string = 'assets/img/usuario.png'; // imagen por defecto

  constructor(
    private router: Router,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit() {
    const usuarioId = localStorage.getItem('usuarioId');
    if (usuarioId) {
      this.usuarioService.obtenerImagen(Number(usuarioId)).subscribe({
        next: (blob) => {
          const objectURL = URL.createObjectURL(blob);
          this.imagenUrl = objectURL; // reemplaza el ícono por la imagen real
        },
        error: () => {
          // si falla, dejamos la imagen por defecto
          this.imagenUrl = 'assets/img/usuario.png';
        },
      });
    }
  }

  // Verifica si hay token guardado
  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt');
  }

  // Redirige al login
  irAlLogin() {
    this.router.navigate(['/login']);
  }

  // Cierra sesión
  cerrarSesion(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('usuarioId');
    this.router.navigate(['/login']);
  }
}
