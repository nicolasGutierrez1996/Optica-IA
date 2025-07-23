import { AbstractControl, ValidationErrors } from '@angular/forms';

export function dniValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  const dniRegex = /^[1-9][0-9]{6,7}$/;

  if (!value || !dniRegex.test(value)) {
    return { dniInvalido: true };
  }

  return null;
}
