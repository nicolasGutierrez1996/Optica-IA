import { Component } from '@angular/core';
import {PortadaComponent} from './portada/portada.component';
import {AnteojoInitComponent} from './anteojo-init/anteojo-init.component';
import {SimuladorInitComponent} from './simulador-init/simulador-init.component';
import {RegistrarseInitComponent} from './registrarse-init/registrarse-init.component';
import {NosotrosInitComponent} from './nosotros-init/nosotros-init.component';
import {ContactoInitComponent} from './contacto-init/contacto-init.component';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [
    PortadaComponent,
    AnteojoInitComponent,
    SimuladorInitComponent,
    RegistrarseInitComponent,
    NosotrosInitComponent,
    ContactoInitComponent
  ],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.css'
})
export class InicioComponent {

}
