import {Component, EventEmitter, inject, OnInit, Output} from '@angular/core';
import {NgClass} from '@angular/common';
import {CuponService} from '../../servicios/cupon.service';
import {UsuarioService} from '../../servicios/usuario.service';
import {Usuario} from '../../model/usuario';
import {Optica} from '../../model/optica';

@Component({
  selector: 'app-inicio-panel',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './inicio-panel.component.html',
  styleUrl: './inicio-panel.component.css'
})
export class InicioPanelComponent implements OnInit {
  protected nombreOptica: string='';
  protected nombrePlan: string='';
  protected diasRestantes: string='';
  protected generandoCupon:boolean=false;
  protected mensajeError:string='';
  protected mensaje:string='';
  protected optica: Optica | null = null;
  protected existeOptica:boolean=false;
  protected codigoCupon:string='';
  protected cuponesUsados:number=0;
  private cuponService:CuponService=inject(CuponService);
  private usuarioService: UsuarioService=inject(UsuarioService);
  @Output() editarDatos: EventEmitter<void> = new EventEmitter();

  ngOnInit() {

    this.usuarioService.obtenerMiPerfil().subscribe({
      next: (resp) => {
        const usuario = resp.data as Usuario | undefined;
        if (!usuario) {
          alert('No se pudo obtener el perfil del usuario');
          return;
        }  if (usuario.optica) {
          console.log("tiene optica");
          this.existeOptica=true;
          this.optica = usuario.optica;
          this.nombreOptica=this.optica.nombre;
          this.verificarSuscripcion(usuario.id!);

          this.cuponService.buscarPorOptica(this.optica!.id!).subscribe({
            next: (resp) => {
              console.log("respuesta",resp.data);
              if (resp.success) {
                this.codigoCupon=resp.data!.codigo;
                if(resp.data!.cantidadReferidas>0){
                  this.cuponesUsados=resp.data!.cantidadReferidas;

                }else{
                  this.cuponesUsados=0;
                }

              }else{
                this.codigoCupon='';
              }
            },error: (err) => {

              this.mensajeError = err.message ? err.message : 'Error al generar cupon';
            }
          })


        }else{
          console.log("no existe optica");
          this.existeOptica=false;
        }
      }
    });



  }

  verMisDatos() {
    this.editarDatos.emit();
  }

  generarCupon() {
      this.generandoCupon = true;
    if(this.existeOptica){
      this.cuponService.generarCupon(this.optica!.id!).subscribe({
        next: (respuesta) => {
          if(respuesta.success){
            console.log("cupon generado:",respuesta);
            this.generandoCupon=false;
            this.codigoCupon=respuesta.data?.codigo!;
            this.mensajeError='';
          }else{
            this.generandoCupon=false;
            this.mensajeError = respuesta.message ? respuesta.message : 'Error al generar cupon';
          }
        },
        error: (err) => {
          this.generandoCupon=false;
          this.mensajeError = err.message ? err.message : 'Error al generar cupon';
        }
      });
    }


      }

  private verificarSuscripcion(id_usuario:number): void {
    this.usuarioService.buscarPorUsuario(id_usuario).subscribe({
      next: (suscripcion) => {
        if(suscripcion && suscripcion.tipoSuscripcion.id==1){
           this.nombrePlan='BASICO';
        }else if(suscripcion && suscripcion.tipoSuscripcion.id==2){
          this.nombrePlan='PREMIUM';

        }else{
          this.nombrePlan='NO TIENE SUSCRIPCION'
        }

        if(this.nombrePlan && suscripcion.fechaVencimiento){
          const hoy = new Date();
          const vencimiento = new Date(suscripcion.fechaVencimiento);

          const diffMs = vencimiento.getTime() - hoy.getTime();
          const dias = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

          this.diasRestantes = Math.max(dias, 0).toString();
        }else{
          this.diasRestantes='0';
        }
      },
      error: () => {
        this.nombrePlan='NO TIENE SUSCRIPCION'
      }
    });
  }

  copiarCupon() {
    navigator.clipboard.writeText(this.codigoCupon).then(() => {
      this.mensaje = '¡Cupón copiado al portapapeles!';
      setTimeout(() => this.mensaje = '', 3000);
    }).catch(() => {
      this.mensajeError = 'Error al copiar el cupón.';
      setTimeout(() => this.mensajeError = '', 3000);
    });
  }

  compartirCuponPorWhatsapp() {
    const mensaje = `¡Hola! Usá este cupón en Óptica IA para obtener un 20% de descuento en tu suscripcion:\n\n${this.codigoCupon}`;
    const url = `https://wa.me/?text=${encodeURIComponent(mensaje)}`;
    window.open(url, '_blank');
  }
}




