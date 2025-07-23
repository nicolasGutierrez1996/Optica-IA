import {UsuarioService} from '../../servicios/usuario.service';
import {AbstractControl, AsyncValidatorFn} from '@angular/forms';
import {catchError, debounceTime, map, of, switchMap} from 'rxjs';

export function emailExistenteValidator(usuarioService: UsuarioService): AsyncValidatorFn {
  return (control: AbstractControl) => {
    if (!control.value) {
      return of(null);
    }

    return of(control.value).pipe(
      debounceTime(300),
      switchMap(email => usuarioService.existeEmail(email)),
      map(response => {
        if (!response.success) {
          return { emailInvalido: true };
        }

        return response.data ? { emailExistente: true } : null;
      }),
      catchError(() => of(null))
    );
  };
}
