import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../services/auth';


export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // 1. Chequeamos si el usuario está logueado y es ADMIN 
  // Esto depende de cómo guardes el rol en tu AuthService
  if (authService.isLoggedIn() && authService.hasRole('ROLE_ADMIN')) {
    return true; // Acceso concedido
  }

  // 2. Si no es admin, lo mandamos al login o al home
  console.warn('Acceso denegado: Se requiere rol ADMIN');
  router.navigate(['/login']);
  return false;
};