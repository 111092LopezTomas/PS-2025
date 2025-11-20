import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent implements OnInit {
  token: string | null = null;

  newPassword: string = '';
  confirmPassword: string = '';

  loadingToken = true; // mientras leemos el token de la URL
  submitting = false; // mientras enviamos al backend
  successMessage = '';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      this.token = params.get('token');
      console.log('Token desde la URL:', this.token);
      this.loadingToken = false;

      if (!this.token) {
        this.errorMessage =
          'El enlace de recuperación es inválido o ha expirado.';
      }
    });
  }

  onSubmit(form: NgForm): void {
    if (!this.token) {
      this.errorMessage =
        'El enlace de recuperación es inválido o ha expirado.';
      return;
    }

    if (form.invalid) {
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.submitting = true;

    this.authService.resetPassword(this.token, this.newPassword).subscribe({
      next: (resp: any) => {
        this.successMessage =
          resp?.message ||
          'Tu contraseña se actualizó correctamente. Ya podés iniciar sesión.';
        this.errorMessage = '';
        this.newPassword = '';
        this.confirmPassword = '';
        form.resetForm();
      },
      error: (err) => {
        console.error('reset-password error', err);
        this.errorMessage =
          err?.error?.message ||
          'No se pudo actualizar la contraseña. Es posible que el enlace haya expirado.';
      },
      complete: () => {
        this.submitting = false;
      },
    });
  }

  irAlLogin(): void {
    this.router.navigate(['/login']);
  }
}
