import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';


export const contrasenasIgualesValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get('password')?.value;
  const confirmar = control.get('confirmar')?.value;

  if (password && confirmar && password !== confirmar) {
    return { contrasenasDistintas: true };
  }
  return null;
};
