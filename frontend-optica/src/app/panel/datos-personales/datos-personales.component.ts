import {Component, EventEmitter, inject, OnInit, Output} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { UsuarioService } from '../../servicios/usuario.service';
import { Usuario } from '../../model/usuario';
import { emailExistenteValidator } from '../../auth/validaciones/email-existente.validator';
import { dniValidator } from '../../validadores/dni.validator';
import {Optica} from '../../model/optica';
import {ConfirmarPasswordComponent} from '../confirmar-password/confirmar-password.component';

@Component({
  selector: 'app-datos-personales',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, ConfirmarPasswordComponent],
  templateUrl: './datos-personales.component.html',
  styleUrl: './datos-personales.component.css'
})
export class DatosPersonalesComponent implements OnInit {
  protected modoEdicion: boolean = false;
  protected formPerfil: FormGroup;
  protected mensajeError:string='';
  protected mensaje:string='';
  protected actualizando: boolean = false;
  protected mostrarConfirmacion:boolean = false;
  @Output() salir = new EventEmitter<void>();

  private usuarioService: UsuarioService = inject(UsuarioService);
  private usuario_id: number=0;

  constructor(private formBuilder: FormBuilder) {
    this.formPerfil = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      apellido: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: [
        '',
        [Validators.required, Validators.email, Validators.maxLength(100)]
      ],
      dni: ['', [Validators.required, dniValidator]]
    });
  }

  ngOnInit(): void {
    this.formPerfil.disable();

    this.usuarioService.obtenerMiPerfil().subscribe({
      next: (resp) => {
        const usuario = resp.data as Usuario | undefined;
        if (!usuario) {
          alert('No se pudo obtener el perfil del usuario');
          return;
        }

        this.usuario_id = usuario.id!;
        this.formPerfil.patchValue({
          nombre: usuario.nombre,
          apellido: usuario.apellido,
          email: usuario.email,
          dni: usuario.dni
        });
      },
      error: (err) => {
        console.error('Error al obtener el perfil del usuario', err);
        alert('Ocurrió un error al obtener los datos del usuario.');
      }
    });
  }

  guardarCambios(): void {
    if (this.formPerfil.invalid || !this.modoEdicion) return;
    this.mostrarConfirmacion = true;
  }

  onToggleEdicion(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.modoEdicion = input.checked;

    if (this.modoEdicion) {
      this.formPerfil.enable();
    } else {
      this.formPerfil.disable();
    }
  }

  irAinicio() {
    this.salir.emit();
  }

  abrirConfirmacion() {
    this.mostrarConfirmacion = true;
  }

  cancelarConfirmacion() {
    this.mostrarConfirmacion = false;
  }

  confirmarAccion() {
    this.actualizando = true;
    this.mensaje = '';
    this.mensajeError = '';

    const datosActualizados = {
      ...this.formPerfil.value
    };

    this.usuarioService.actualizarPerfil(this.usuario_id, datosActualizados).subscribe({
      next: (resp) => {
        if (resp.success) {
          this.mensaje = 'Actualización exitosa';
          this.mensajeError = '';
          this.actualizando = false;
          this.modoEdicion = false;
          this.formPerfil.disable();
          setTimeout(() => this.mensaje = '', 3000);
        } else {
          this.mensajeError = resp.message || 'Error al actualizar';
          this.mensaje = '';
          this.actualizando = false;
        }
      },
      error: (err) => {
        this.mensajeError = err.error.message || 'Error al actualizar';
        this.mensaje = '';
        this.actualizando = false;
      }
    });

    this.mostrarConfirmacion = false;
  }



}
