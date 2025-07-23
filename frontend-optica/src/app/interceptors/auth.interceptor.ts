import { HttpInterceptorFn } from '@angular/common/http';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const token = typeof window !== 'undefined' ? localStorage.getItem('jwt') : null;

  const PUBLIC_URLS = [
    '/api/login',
    '/api/registro',
    '/api/email/contacto',
    '/api/usuarios/recuperar',
    '/api/usuarios/verificar-token',
    '/api/provincia'
  ];

  const isPublic =
    PUBLIC_URLS.some(url => req.url.startsWith(url)) ||
    /^\/api\/provincia\/\d+\/localidades$/.test(req.url);

  if (isPublic || !token) {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
