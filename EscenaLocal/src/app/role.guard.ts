import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router
} from '@angular/router';
import { AuthService } from './services/auth.service'; // ajustá la ruta

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const expectedRole = route.data['role'] as string;
    const tokenRole = this.auth.getRoleFromToken(); // ya lo tenés en tu service

    // si no hay token o no hay rol → afuera
    if (!tokenRole) {
      this.router.navigate(['/login']);
      return false;
    }

    // si no coincide → forbidden
    if (expectedRole && tokenRole !== expectedRole) {
      this.router.navigate(['/forbidden']);
      return false;
    }

    return true;
  }
}
