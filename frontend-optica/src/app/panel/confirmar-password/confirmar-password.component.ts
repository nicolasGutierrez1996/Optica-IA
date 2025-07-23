import {Component, EventEmitter, inject, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {UsuarioLoginDataService} from '../../servicios/usuario-login-data.service';
import {UsuarioService} from '../../servicios/usuario.service';

@Component({
  selector: 'app-confirmar-password',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './confirmar-password.component.html',
  styleUrl: './confirmar-password.component.css'
})
export class ConfirmarPasswordComponent {
  password: string = '';
  @Output() onConfirm = new EventEmitter<void>();
  @Output() onCancel = new EventEmitter<void>();
  protected mensajeError:string='';
  protected validando:boolean=false;
  private usuarioLoginService:UsuarioLoginDataService=inject(UsuarioLoginDataService);
  private usuarioService:UsuarioService=inject(UsuarioService);



  confirmar() {
     this.validando=true;
    const usuarioId=this.usuarioLoginService.getUsuarioId();

    if(!usuarioId){
      return;
    }

    this.usuarioService.validarContrasenia(usuarioId,this.password).subscribe({
      next: (response)=> {
        if(response.success){
          this.validando=false;
          this.password = '';
          this.onConfirm.emit();

        }else{
          this.validando=false;
          this.password = '';
          this.mensajeError=response.message?response.message : 'Error al validar contraseña';
        }

      },error: (err)=> {
        this.validando=false;
        this.password = '';
        this.mensajeError=err.error.message?err.error.message : 'Error al validar contraseña';

      }
    })





  }

  cancelar() {
    this.onCancel.emit();
    this.password = '';
  }
}
