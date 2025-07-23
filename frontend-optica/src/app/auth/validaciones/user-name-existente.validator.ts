import {UsuarioService} from '../../servicios/usuario.service';
import {AbstractControl, AsyncValidatorFn} from '@angular/forms';
import {catchError, debounceTime, map, of} from 'rxjs';

export function usernameExistenteValidator(usuarioService: UsuarioService): AsyncValidatorFn {
  return (control: AbstractControl) => {
    if (!control.value || control.value.length < 6) {
      return of(null);
    }

    return usuarioService.existeUserName(control.value).pipe(
      debounceTime(300),
      map(res => {
        if (!res.success) {
          return { usernameInvalido: true };
        } else if (res.data === true) {
          return { usernameExistente: true };
        }
        return null;
      }),
      catchError(() => of(null))
    );
  };
}
