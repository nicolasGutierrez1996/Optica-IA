import {AfterViewInit, Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgClass} from '@angular/common';
import {contrasenasIgualesValidator} from '../validaciones/contrasenas-iguales.validator';
import {UsuarioService} from '../../servicios/usuario.service';
import {catchError, of} from 'rxjs';
import {usernameExistenteValidator} from '../validaciones/user-name-existente.validator';
import {emailExistenteValidator} from '../validaciones/email-existente.validator';
import {Router} from '@angular/router';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.css'
})
export class RegistroComponent implements AfterViewInit  {
  protected formGroup:FormGroup;
  protected enProceso:boolean = false;
  protected verPassword: boolean = false;
  protected verConfirmar: boolean = false;
  private usuarioService:UsuarioService=inject(UsuarioService);
  protected mensajeExito: boolean=false;
  protected ocurrioError: boolean=false;
  protected mensajeError: string='';
  protected mensaje:string='';
  private router: Router=inject(Router);


  ngAfterViewInit(): void {
    if (typeof window !== 'undefined' && typeof document !== 'undefined') {
      setTimeout(() => {
        const header = document.getElementById('header');
        const offset = header ? header.clientHeight - 80 : 100;

        window.scrollTo({
          top: offset,
          behavior: 'smooth'
        });
      }, 0);
    }
  }

  constructor(private formBuilder: FormBuilder) {
    this.formGroup = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      apellido: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      username: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)],
        [usernameExistenteValidator(this.usuarioService)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(100)],
        [emailExistenteValidator(this.usuarioService)]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
      confirmar: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
    }, {
      validators: contrasenasIgualesValidator
    });
  }


  generarUsername() {
    this.usuarioService.autogenerarUserName(this.formGroup.value.nombre, this.formGroup.value.apellido)
      .pipe(
        catchError((error) => {
          alert("No se pudo autogenerar el nombre de usuario");
          return of(null); // Devuelve un observable que emite null para continuar el flujo
        })
      )
      .subscribe((resp) => {
        if (resp?.success && resp.data) {
          this.formGroup.controls['username'].setValue(resp.data);
        }
      });
  }

  registrarUsuario() {
    this.enProceso = true;
    this.ocurrioError = false;
    this.mensajeExito = false;
    this.mensajeError = '';
    this.mensaje = '';

    this.usuarioService.crearUsuario(this.formGroup.value).subscribe({
      next: (response) => {
        if (response.success) {
          this.enProceso = false;
          this.mensajeExito = true;
          localStorage.setItem('emailPendienteVerificacion', this.formGroup.value.email);
          this.formGroup.reset();

          this.mensaje = '¡Cuenta creada exitosamente! Revisá tu correo para verificar tu cuenta.';

          setTimeout(() => {
            this.router.navigate(['/verificacion-enviada']);
          }, 5000);
        } else {
          this.enProceso = false;
          this.ocurrioError = true;
          this.mensajeError = response.message as string;
          console.error('Error:', response.message);
          setTimeout(() => {
            this.ocurrioError = false;
          }, 5000);
        }
      },
      error: (err) => {
        if (err.status === 400 && err.error.errores) {
          console.error('Errores de validación:', err.error.errores);
          this.enProceso = false;
          this.ocurrioError = true;
          this.mensajeError = 'Errores de formato: ' + JSON.stringify(err.error.errores);
          setTimeout(() => {
            this.ocurrioError = false;
          }, 5000);
        } else {
          console.error('Error inesperado:', err);
          this.enProceso = false;
          this.ocurrioError = true;
          this.mensajeError = 'Ocurrió un error inesperado, vuelva a intentar en unos minutos.';
          setTimeout(() => {
            this.ocurrioError = false;
          }, 5000);
        }
      }
    });
  }

  irAlogin(): void {
    this.router.navigate(['/login']);
  }
}
