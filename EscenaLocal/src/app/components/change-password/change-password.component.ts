import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-change-password',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent{

  loading = false;
  errorMessage = '';
  successMessage = '';

  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    
    this.form = this.fb.group({
      actual: ['', Validators.required],
      nueva: ['', [Validators.required, Validators.minLength(6)]],
      repetir: ['', Validators.required]
    });
  }

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) return;

    if (this.form.value.nueva !== this.form.value.repetir) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    this.loading = true;

    this.authService.cambiarPassword({
      actual: this.form.value.actual!,
      nueva: this.form.value.nueva!
    }).subscribe({
      next: () => {
        this.successMessage = 'Contraseña actualizada correctamente';
        this.loading = false;
        this.form.reset();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error al cambiar la contraseña';
        this.loading = false;
      }
    });
  }
}
