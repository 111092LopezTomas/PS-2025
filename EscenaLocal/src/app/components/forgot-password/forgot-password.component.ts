import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  email: string = '';
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService) {}

  onSubmit(form: NgForm): void {
    if (form.invalid) {
      return;
    }

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.authService.requestPasswordReset(this.email).subscribe({
      next: (resp: any) => {
        this.successMessage =
          resp?.message ||
          'Si el correo está registrado, te enviaremos instrucciones.';
        this.errorMessage = '';
        form.resetForm();
      },
      error: (err) => {
        console.error('forgot-password error', err);
        this.errorMessage =
          err?.error?.message ||
          'Ocurrió un error al procesar la solicitud. Intenta nuevamente.';
      },
      complete: () => {
        this.loading = false;
      },
    });
  }
}
