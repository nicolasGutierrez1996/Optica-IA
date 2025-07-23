import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

function isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp;
    const now = Math.floor(Date.now() / 1000);
    return exp < now;
  } catch (e) {
    return true; // Si falla el decode, lo tomamos como inválido
  }
}

export const AuthGuard: CanActivateFn = () => {
  const router = inject(Router);
  console.log('Ejecutando AuthGuard');
  if (typeof window === 'undefined') return false;

  const token = localStorage.getItem('jwt');

  if (!token || isTokenExpired(token)) {
    localStorage.removeItem('jwt');
    localStorage.removeItem('usuario');
    router.navigate(['/login']);
    return false;
  }

  return true;
};
