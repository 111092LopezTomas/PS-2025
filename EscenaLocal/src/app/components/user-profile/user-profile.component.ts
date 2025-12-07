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
  idArtista: number | null = null;
  idProductor: number | null = null;
  loading = true;
  error = '';
  imagenUrl: string | null = null;

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.authService.getUserId();
    if (!id) {
      this.error = 'No se pudo obtener el usuario actual.';
      this.loading = false;
      return;
    }

    this.usuarioService.getUsuarioById(id).subscribe({
      next: (u) => {
        this.usuario = u;

        // 🔹 Cargar IDs desde el DTO
        this.idArtista = u.idArtista ?? null;
        this.idProductor = u.idProductor ?? null;

        // 🔹 Cargar imagen si tu backend lo expone así
        this.imagenUrl = `http://localhost:8080/auth/${u.id}/imagen`;

        this.loading = false;
      },
      error: () => {
        this.error = 'No se pudo cargar el perfil';
        this.loading = false;
      }
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
    if (!this.usuario) return;

    // 🔹 ahora usamos idArtista o idProductor del DTO
    if (this.esProductor() && this.idProductor) {
      this.router.navigate([`/eventos/productor/${this.idProductor}`]);

    } else if (this.esArtista() && this.idArtista) {
      this.router.navigate([`/eventos/artista/${this.idArtista}`]);

    } else {
      this.router.navigate(['/eventos']);
    }
  }
}
