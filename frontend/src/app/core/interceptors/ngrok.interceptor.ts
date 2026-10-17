import { HttpInterceptorFn } from '@angular/common/http';

export const ngrokInterceptor: HttpInterceptorFn = (req, next) => {
  // Clonamos la petición para agregarle el header que Ngrok exige
  // para saltarse la molesta pantalla azul de advertencia.
  const ngrokReq = req.clone({
    setHeaders: {
      'ngrok-skip-browser-warning': 'true'
    }
  });

  return next(ngrokReq);
};
