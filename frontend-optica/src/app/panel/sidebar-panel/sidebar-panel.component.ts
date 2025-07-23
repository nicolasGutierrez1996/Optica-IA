import {Component, inject, Output, EventEmitter, Input} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-sidebar-panel',
  standalone: true,
  imports: [],
  templateUrl: './sidebar-panel.component.html',
  styleUrl: './sidebar-panel.component.css'
})
export class SidebarPanelComponent {
   private router: Router=inject(Router);
  @Output() seccionSeleccionada: EventEmitter<string> = new EventEmitter<string>();
  @Input() seccionActiva: string = '';
  protected mostrarSubmenu = {
    optica: false,
    anteojo: false,
    lente:false
  };

  logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('usuario');
    this.router.navigate(['/login']);
  }

  emitirSeccion(seccion: string) {
    this.seccionSeleccionada.emit(seccion);
  }

}
