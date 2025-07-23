import {Component, EventEmitter, inject, Output} from '@angular/core';
import {Router} from '@angular/router';
import {OpticaService} from '../../servicios/optica.service';
import {UsuarioLoginDataService} from '../../servicios/usuario-login-data.service';
import {ConfirmarPasswordComponent} from '../confirmar-password/confirmar-password.component';

@Component({
  selector: 'app-eliminar-optica',
  standalone: true,
  imports: [
    ConfirmarPasswordComponent
  ],
  templateUrl: './eliminar-optica.component.html',
  styleUrl: './eliminar-optica.component.css'
})
export class EliminarOpticaComponent {
  private router:Router=inject(Router);
  @Output() irInicio = new EventEmitter<void>();
  protected eliminando: boolean=false;
  private opticaService:OpticaService=inject(OpticaService);
  protected mensajeError: string='';
  protected mensaje: string='';
  protected mostrarConfirmacion: boolean=false;


  abrirConfirmacion() {
    this.mostrarConfirmacion = true;
  }

  cancelarConfirmacion() {
    this.mostrarConfirmacion = false;
  }

  confirmarAccion() {
    this.mostrarConfirmacion = false;
    this.eliminarOptica();
  }
  eliminarOptica() {
       this.eliminando=true;

    const opticaJson = localStorage.getItem('optica');
    console.log("opticajson",opticaJson);
    if (opticaJson) {
      const optica = JSON.parse(opticaJson);
      console.log("opticajson",optica);

      this.opticaService.eliminar(optica.id).subscribe({
        next: (resp) => {
          if (resp.success) {
            localStorage.removeItem('jwt');
            localStorage.removeItem('usuario');
            localStorage.removeItem('optica');
            this.mensajeError='';
            this.eliminando=false;
            this.mensaje = 'Óptica eliminada correctamente';
            setTimeout(() => {
              this.router.navigate(['']);
            }, 3000);

          } else {
            this.mensajeError = resp.message || 'No se pudo eliminar.';
            this.eliminando=false;
          }
        },
        error: (err) => {
          console.error(err);
          this.mensajeError = err.message|| 'Error inesperado.';
          this.eliminando=false;
        }
      });
    }else{
      this.eliminando=false;
      this.mensajeError='No tienes ninguna optica creada';
      console.log("No tiene optica asignada");

    }

  }

  volverAinicio() {
        this.irInicio.emit();
  }
}
