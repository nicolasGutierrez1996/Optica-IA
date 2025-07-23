import {Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Optica} from '../../model/optica';
import {CommonModule} from '@angular/common';
import {dniValidator} from '../../validadores/dni.validator';
import {UsuarioService} from '../../servicios/usuario.service';
import {UsuarioLoginDataService} from '../../servicios/usuario-login-data.service';
import {ConfirmarPasswordComponent} from '../confirmar-password/confirmar-password.component';

@Component({
  selector: 'app-suscripcion-optica',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, ConfirmarPasswordComponent],
  templateUrl: './suscripcion-optica.component.html',
  styleUrl: './suscripcion-optica.component.css'
})
export class SuscripcionOpticaComponent implements OnInit, OnDestroy {
  protected formulario: FormGroup;
  @Input() optica!: Optica;
  @Input() vieneDeEdicion: boolean = false;
  @Output() seGeneroLinkPago = new EventEmitter<Optica>();

  private usuarioService:UsuarioService=inject(UsuarioService);
  private usuarioLoginDataService = inject(UsuarioLoginDataService);
  protected estado: string='';
  protected mostrarConfirmacion: boolean=false;

  ngOnDestroy(): void {
    this.vieneDeEdicion = false;
  }
  ngOnInit() {

    this.usuarioService.buscarPorUsuario(this.usuarioLoginDataService.getUsuarioId()!).subscribe({
      next: (suscripcion) => {
        console.log("suscripcion.estado",suscripcion.estado);
        if(this.vieneDeEdicion){
          this.estado = 'edicion';
          this.usuarioService.obtenerDniPorId(this.usuarioLoginDataService.getUsuarioId()!).subscribe({
            next: (respuesta) => {
              if (respuesta.success && respuesta.data) {
                this.formulario.patchValue({
                  dni: respuesta.data
                })
              } else {
                console.warn('⚠️ No se encontró el DNI:', respuesta.message);
              }
            },
            error: (error) => {
              console.error('❌ Error al obtener DNI:', error);
            }
          });
        }else{
          this.estado = suscripcion.estado === 'APROBADA' ? 'aprobada' : 'nueva';

        }

      },
      error: () => {
        this.estado = 'nueva';
      }
    });
  }

  constructor(private fb: FormBuilder) {
    this.formulario = this.fb.group({
      plan: ['premium', Validators.required],
      dni: ['', [Validators.required, dniValidator]],
      cupon: ['']
    });
  }


  pagar() {

    const usuarioId = this.usuarioLoginDataService.getUsuarioId();

    const { plan, dni, cupon } = this.formulario.value;
    console.log('Plan:', plan);
    console.log('DNI:', dni);
    console.log('Cupón:', cupon);

    if (!this.optica ) {
      alert('Error: faltan datos necesarios para suscribirse.');
      return;
    }
    if (usuarioId === null) {
      alert('Error: no se pudo obtener el ID del usuario.');
      return;
    }
    this.usuarioService.suscribirse({
      usuarioId: usuarioId,
      dniUsuario: this.formulario.value.dni,
      opticaId: this.optica!.id!,
      tipoSuscripcionId: this.formulario.value.plan === 'premium' ? 2 : 1,
      cupon: this.formulario.value.cupon || ''
    }).subscribe({
      next: (resp) => {
        if (resp.success && resp.linkPago) {
          const nuevaVentana = window.open('', '_blank');
          nuevaVentana!.location.href = resp.linkPago;
          this.seGeneroLinkPago.emit();

        } else {
          alert(resp.message || 'Error al iniciar la suscripción');
        }
      },
      error: (err) => {
        console.error(err);
        alert(err.message ||'Error inesperado al procesar la suscripción');
      }
    });


  }

  abrirConfirmacion() {
    this.mostrarConfirmacion = true;
  }

  cancelarConfirmacion() {
    this.mostrarConfirmacion = false;
  }

  confirmarAccion() {
    this.mostrarConfirmacion = false;
    this.pagar();
  }

}
