import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {usernameExistenteValidator} from '../validaciones/user-name-existente.validator';
import {emailExistenteValidator} from '../validaciones/email-existente.validator';
import {contrasenasIgualesValidator} from '../validaciones/contrasenas-iguales.validator';
import {UsuarioService} from '../../servicios/usuario.service';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgClass],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  protected mensajeError: string='';
  protected loginForm: FormGroup;
  protected error:boolean=false;
  protected ingresando: boolean=false;
  private router: Router=inject(Router);
  private usuarioService: UsuarioService=inject(UsuarioService);
  protected verPassword:boolean= false;


  constructor(private formBuilder: FormBuilder) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)],
       ],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]],
    });
  }


  iniciarSesion() {
    const { username, password } = this.loginForm.value;

    this.usuarioService.esUsuarioVerificado(username).subscribe({
      next: (verificadoResp) => {
        if (!verificadoResp.success) {
          console.log("mensaje del back:", verificadoResp.message);
          this.mensajeError = verificadoResp.message ?? 'El usuario no está verificado';
          this.error = true;
          return;
        }

        // CONTINUAR: Verificar contraseña solo si el usuario está verificado
        this.usuarioService.esContraseniaCorrecta(username, password).subscribe({
          next: (passResp) => {
            if (!passResp.success) {
              this.mensajeError = passResp.message ?? 'Contraseña incorrecta';
              this.error = true;
              return;
            }

            // CONTINUAR: Hacer login
            this.usuarioService.login(username).subscribe({
              next: (loginResp) => {
                if (loginResp.success && loginResp.data) {
                  localStorage.setItem('jwt', loginResp.data.token);
                  localStorage.setItem('usuario', JSON.stringify(loginResp.data.usuario));
                  const optica = loginResp.data.usuario.optica;
                  if (optica) {
                    localStorage.setItem('optica', JSON.stringify(optica));
                  } else {
                    localStorage.removeItem('optica');
                  }
                  this.router.navigate(['/panel']);
                } else {
                  this.mensajeError = loginResp.message ?? 'Error al iniciar sesión';
                  this.error = true;
                }
              },
              error: () => {
                this.mensajeError = 'Error del servidor al hacer login';
                this.error = true;
              }
            });
          },
          error: (err) => {
            this.mensajeError = err.error?.message ?? 'Error al verificar contraseña';
            this.error = true;
          }
        });
      },
      error: (err) => {
        this.mensajeError = err.error?.message ?? 'Error al verificar al usuario';
        this.error = true;
      }
    });
  }
}
