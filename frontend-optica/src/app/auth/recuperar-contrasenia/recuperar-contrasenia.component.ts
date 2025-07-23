import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UsuarioService } from '../../servicios/usuario.service';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-recuperar-contrasenia',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, NgClass],
  templateUrl: './recuperar-contrasenia.component.html',
  styleUrl: './recuperar-contrasenia.component.css'
})
export class RecuperarContraseniaComponent {
  private router: Router = inject(Router);
  protected forms: FormGroup;
  protected error: boolean = false;
  protected mensajeError: string = '';
  protected emailEnviado: boolean = false;
  protected enviandoMail: boolean = false;
  protected cambiandoPassword: boolean = false;
  protected tokenCorrecto: boolean = false;
  protected comprobandoToken: boolean = false;
  private userService: UsuarioService = inject(UsuarioService);
  protected mensajeExito: boolean = false;
  protected verConfirmar: boolean = false;
  protected verPassword: boolean = false;

  // 🆕 NUEVAS VARIABLES PARA BLOQUEAR CAMPOS
  protected emailBloqueado: boolean = false;
  protected tokenBloqueado: boolean = false;

  constructor(formBuilder: FormBuilder) {
    this.forms = formBuilder.group({
      email: ['', [
        Validators.required,
        Validators.email,
        Validators.minLength(5),
        Validators.maxLength(100)
      ]],
      token: ['', [
        Validators.required
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(100)
      ]],
      confirmacion: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(100)
      ]]
    });
  }

  enviarMail() {
    const email = this.forms.value.email;
    this.enviandoMail = true;

    this.userService.recuperarContrasenia(email).subscribe({
      next: (response) => {
        if (response.success) {
          console.log('✅ Éxito:', response);
          this.emailEnviado = true;
          this.emailBloqueado = true; // 🔒 bloqueamos email
          this.mensajeError = '';
        } else {
          console.log('error:', response);
          this.emailEnviado = false;
          this.mensajeError = response.message ?? 'Error al enviar email de recuperación';
        }
        this.enviandoMail = false;
      },
      error: (error) => {
        console.error('❌ Error al recuperar contraseña:', error);
        this.emailEnviado = false;
        this.enviandoMail = false;
        this.mensajeError = error.error?.message ?? 'Error al enviar email de recuperación';
      }
    });
  }

  validarToken() {
    this.comprobandoToken = true;

    const email = this.forms.value.email;
    const token = this.forms.value.token;

    this.userService.verificarToken(email, token).subscribe({
      next: () => {
        this.tokenCorrecto = true;
        this.tokenBloqueado = true; // 🔒 bloqueamos token
        this.mensajeError = '';
        this.comprobandoToken = false;
      },
      error: (error) => {
        this.tokenCorrecto = false;
        this.comprobandoToken = false;
        this.mensajeError = error.error?.message || 'Error al verificar token';
      }
    });
  }

  cambiarContrasenia() {
    this.cambiandoPassword = true;

    const email = this.forms.value.email;
    const contrasenia = this.forms.value.password;
    const confirmacion = this.forms.value.confirmacion;

    if (contrasenia !== confirmacion) {
      this.mensajeError = 'Las contraseñas son distintas';
      this.cambiandoPassword = false;
      return;
    }

    this.userService.actualizarClave(email, contrasenia).subscribe({
      next: () => {
        this.mensajeExito = true;
        this.mensajeError = '';
        this.tokenCorrecto = false;
        this.cambiandoPassword = false;
        this.emailEnviado = false;

        // 🔄 desbloquear todo al finalizar
        this.emailBloqueado = false;
        this.tokenBloqueado = false;
        this.forms.reset();

        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.mensajeError = err.error?.mensaje || 'Ocurrió un error al actualizar la contraseña';
        this.cambiandoPassword = false;
      }
    });
  }
}
