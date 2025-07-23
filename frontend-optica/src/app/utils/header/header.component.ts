import {Component, inject, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit{
  private router: Router=inject(Router);

  ngOnInit() {
    console.log("HeaderComponent montado");
  }

  irASeccion(id: string): void {
    console.log("Ir a:", id);

    const hacerScroll = () => {
      const elemento = document.getElementById(id);
      if (elemento) {
        const offsetTop = elemento.getBoundingClientRect().top + window.scrollY;
        let offset = 0;

        switch (id) {
          case 'portada-section':
            offset = -80;
            break;
          case 'simulador-section':
            offset = -20;
            break;
          case 'registrarse-section':
            offset = -30;
            break;
          case 'nosotros-section':
            offset = 10;
            break;
          case 'contacto-section':
            offset = 0;
            break;
          default:
            offset = -40;
            break;
        }

        window.scrollTo({
          top: offsetTop + offset,
          behavior: 'smooth'
        });

      } else {
        console.warn(`Elemento con id '${id}' no encontrado`);
      }
    };

    if (this.router.url !== '/') {
      this.router.navigateByUrl('/').then(() => {
        setTimeout(() => hacerScroll(), 200);
      });
    } else {
      hacerScroll();
    }
  }

  irAlogin(): void {
    this.router.navigate(['/login']);
  }
}
