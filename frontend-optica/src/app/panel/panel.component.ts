import {Component, inject, OnInit} from '@angular/core';
import {SidebarPanelComponent} from './sidebar-panel/sidebar-panel.component';
import {DatosOpticaComponent} from './datos-optica/datos-optica.component';
import {Optica} from '../model/optica';
import {UsuarioLoginDataService} from '../servicios/usuario-login-data.service';
import {SuscripcionOpticaComponent} from './suscripcion-optica/suscripcion-optica.component';
import {InicioPanelComponent} from './inicio-panel/inicio-panel.component';
import {EliminarOpticaComponent} from './eliminar-optica/eliminar-optica.component';
import {DatosPersonalesComponent} from './datos-personales/datos-personales.component';

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [
    SidebarPanelComponent,
    DatosOpticaComponent,
    SuscripcionOpticaComponent,
    InicioPanelComponent,
    EliminarOpticaComponent,
    DatosPersonalesComponent
  ],
  templateUrl: './panel.component.html',
  styleUrl: './panel.component.css'
})
export class PanelComponent implements OnInit {

  protected seccionActiva: string = 'inicio';
  protected registroExitoso = false;
  protected opticaGuardada: Optica| null = null;
  protected yaTieneOptica: boolean=false;
  private usuarioLoginDataService:UsuarioLoginDataService = inject(UsuarioLoginDataService);
  protected irInicio: boolean=false;
  modoEdicion: any;
  protected vieneDeEdicion: boolean=false;

  ngOnInit() {
    const opticaJson = localStorage.getItem('optica');
    console.log("opticajson",opticaJson);
    if (opticaJson) {
      try {
        const optica = JSON.parse(opticaJson);
        this.yaTieneOptica = !!optica?.id;
        this.opticaGuardada = optica;
      } catch (e) {
        console.error('Error al parsear la óptica guardada:', e);
        this.yaTieneOptica = false;
      }
    }
  }
  cambiarSeccion(seccion: string): void {
    if (seccion === 'registrar-optica') {
      console.log("ya tiene optica:",this.yaTieneOptica);
      this.seccionActiva = this.yaTieneOptica ? 'suscripcion-optica' : 'registrar-optica';
    } else {
      this.seccionActiva = seccion;
    }
  }
  registroCompletado(optica: Optica) {
    this.opticaGuardada = optica;
    this.registroExitoso = true;
  }

  irASuscripcion(optica: Optica): void {
    this.opticaGuardada = optica;
    this.seccionActiva = 'suscripcion-optica';
  }
  irAinicio():void{
    this.seccionActiva = 'inicio';
    this.vieneDeEdicion = false;

  }

  setEsEdicion(valor: boolean) {
    this.vieneDeEdicion = valor;
  }


  irAeditarDatos() {
    this.seccionActiva = 'editar-datos';

  }
}
