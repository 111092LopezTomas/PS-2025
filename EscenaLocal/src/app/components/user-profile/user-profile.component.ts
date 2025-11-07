import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-profile.component.html',
})
export class UserProfileComponent implements OnInit {
  usuario: any = null;
  loading = true;
  error = '';
  imagenUrl: string | null = null;

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService
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
}
