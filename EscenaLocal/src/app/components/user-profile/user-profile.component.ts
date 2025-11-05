import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuario.service';


interface UsuarioPerfil {
  id: number;
  username: string;
  nombreCompleto: string;
  email: string;
  rol: string;
  fechaAlta?: string;
  imagenUrl?: string;
}

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
})
export class UserProfileComponent implements OnInit {
  loading = true;
  error = '';
  usuario?: UsuarioPerfil;

  constructor(private userService: UsuarioService) {}

  ngOnInit(): void {
  const userId = localStorage.getItem('userId');

  if (!userId) {
    this.error = 'Usuario no autenticado.';
    this.loading = false;
    return;
  }

  this.userService.getUsuarioById(+userId).subscribe({
    next: (data) => {
      this.usuario = data;
      this.loading = false;
    },
    error: () => {
      this.error = 'No se pudo cargar el usuario.';
      this.loading = false;
    },
  });
}

}
