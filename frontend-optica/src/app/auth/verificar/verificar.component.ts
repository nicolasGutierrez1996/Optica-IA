import {Component, inject, OnInit} from '@angular/core';
import {VerificarService} from '../../servicios/verificar.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-verificar',
  standalone: true,
  imports: [],
  templateUrl: './verificar.component.html',
  styleUrl: './verificar.component.css'
})
export class VerificarComponent implements OnInit {

  private verificarService: VerificarService=inject(VerificarService);
  private router: Router=inject(Router);
  private route:ActivatedRoute=inject(ActivatedRoute);
  verificando = true;
  verificado: boolean | null = null;
  mensaje = 'Estamos verificando tu cuenta...';




  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (token) {
      this.verificarService.verificarMail(token).subscribe({
        next: (resp: any) => {
          this.verificado = true;
          this.mensaje = resp.mensaje || 'Cuenta verificada exitosamente';
          this.verificando = false;

          setTimeout(() => this.router.navigate(['/login']), 3000);
        },
        error: (err) => {
          this.verificado = false;
          this.mensaje = err.error?.mensaje || 'No se pudo verificar la cuenta';
          this.verificando = false;
        }
      });
    } else {
      this.verificado = false;
      this.verificando = false;
      this.mensaje = 'Token no válido o ausente.';
    }
  }
}
