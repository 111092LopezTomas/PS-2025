import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, RegisterRequest } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {

  model = {
    username: '',
    password: '',
    email: ''
  };

  selectedFile: File | null = null;
  error: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit() {
    const data: RegisterRequest = {
      username: this.model.username,
      password: this.model.password,
      email: this.model.email,
      imagen: this.selectedFile || undefined
    };

    this.authService.register(data, 1).subscribe({
      next: (res) => {
        localStorage.setItem('usuarioId', res.userId.toString());
        this.router.navigate(['/']);
      },
      error: () => {
        this.error = 'Error al registrarse';
      }
    });
  }

  irALogin() {
    this.router.navigate(['/login']);
  }
}
