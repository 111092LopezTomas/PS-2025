import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './user-profile.component.html',
})
export class UserProfileComponent implements OnInit {

  usuario: any = null;
  loading = true;
  error = '';
  imagenUrl: string | null = null;

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // sacamos el id del localStorage (lo guardamos en el login)
    const id = this.authService.getUserId();
    if (!id) {
      this.error = 'No se pudo obtener el usuario actual.';
      this.loading = false;
      return;
    }

    this.usuarioService.getUsuarioById(id).subscribe({
      next: (u) => {
        this.usuario = u;
        // si tu back sirve la imagen en /auth/{id}/imagen
        this.imagenUrl = `http://localhost:8080/auth/${u.id}/imagen`;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudo cargar el perfil';
        this.loading = false;
      },
    });
  }

  esArtista(): boolean {
    return this.usuario?.rol === 'ROL_ARTISTA';
  }

  esProductor(): boolean {
    return this.usuario?.rol === 'ROL_PRODUCTOR';
  }

  editarPerfil() {
    this.router.navigate(['/perfil/editar']);
  }

  crearEvento() {
    this.router.navigate(['/eventos/nuevo']);
  }

  verEventos(): void {
  const userId = this.authService.getUserId();
  if (!userId || !this.usuario) return;

  if (this.esProductor()) {
    this.router.navigate([`/eventos/productor/${userId}`]);
  } else if (this.esArtista()) {
    this.router.navigate([`/eventos/artista/${userId}`]);
  } else {
    // opcional: si es un usuario normal sin rol especial
    this.router.navigate(['/eventos']);
  }
}

}
