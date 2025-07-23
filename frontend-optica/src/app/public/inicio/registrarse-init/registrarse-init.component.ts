import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-registrarse-init',
  standalone: true,
  imports: [],
  templateUrl: './registrarse-init.component.html',
  styleUrl: './registrarse-init.component.css'
})
export class RegistrarseInitComponent {
  private router: Router=new Router();

  redirigirAregistrarse() {
    this.router.navigate(['/registrarse']);

  }
}
