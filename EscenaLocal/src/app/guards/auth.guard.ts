import { Injectable } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const AuthGuard: CanActivateFn = (route, state) => {
  const token = localStorage.getItem('jwt');
  const router = new Router();

  if (token) {
    // Podés agregar validación de expiración si querés
    return true;
  } else {
    router.navigate(['/login']);
    return false;
  }
};
