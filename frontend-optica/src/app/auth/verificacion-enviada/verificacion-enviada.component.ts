import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {VerificarService} from '../../servicios/verificar.service';

@Component({
  selector: 'app-verificacion-enviada',
  standalone: true,
  imports: [],
  templateUrl: './verificacion-enviada.component.html',
  styleUrl: './verificacion-enviada.component.css'
})
export class VerificacionEnviadaComponent implements OnDestroy, OnInit {

  private verificarService: VerificarService=inject(VerificarService);
  protected reenviando:boolean = false;
  protected error: boolean = false;
  protected enviado: boolean = false;
  protected mensajeError:string = '';
  protected mensaje:string = '';
  protected tiempoRestanteTexto: string = '';
  private intervalo: any;

  ngOnInit(): void {
    const cooldown = localStorage.getItem('reenviarCooldown');
    if (cooldown) {
      const tiempoTranscurrido = Date.now() - parseInt(cooldown, 10);
      const tiempoLimite = 0.5 * 60 * 1000;
      if (tiempoTranscurrido < tiempoLimite) {
        this.iniciarContador(tiempoLimite - tiempoTranscurrido);
      }
    }
  }

  ngOnDestroy(): void {
    if (this.intervalo) {
      clearInterval(this.intervalo);
    }
  }


  reenviarEmail() {
    this.resetEstados();

    const email = localStorage.getItem('emailPendienteVerificacion');
    console.log("email:",email);
    if(!email || email === 'null'){
      this.error=true;
      this.mensajeError='No se encontro email para reenviar';
      return;
    }

    const cooldown = localStorage.getItem('reenviarCooldown');
    const ahora = Date.now();

    if (cooldown) {
      const tiempoTranscurrido = ahora - parseInt(cooldown, 10);
      const tiempoLimite = 0.5 * 60 * 1000;

      if (tiempoTranscurrido < tiempoLimite) {
        this.reenviando = false;
        this.error = true;
        this.iniciarContador(tiempoLimite - tiempoTranscurrido);
        return;
      }
    }
    console.log("Email reenvio:",email);
    this.verificarService.reenviarMailVerificacion(email).subscribe({
      next: (resp) => {
        this.reenviando = false;
        this.mensaje='Correo enviado correctamente verifique su casilla de email o reintente dentro de 3 minutos';
        this.enviado = true;
        localStorage.setItem('reenviarCooldown', ahora.toString());
        this.iniciarContador(0.5 * 60 * 1000);
        return;

      },
      error: () => {
        this.reenviando = false;
        this.error=true;
        this.mensajeError='Error al reenviar el correo. Vuelva a intentar';
        return;
      }
    });
  }

  private resetEstados(): void {
    this.reenviando = true;
    this.error = false;
    this.enviado = false;
    this.mensaje = '';
    this.mensajeError = '';
  }


  private iniciarContador(tiempoMs: number): void {
    const fin = Date.now() + tiempoMs;

    this.actualizarTiempoRestante(fin); // mostrar inmediatamente

    this.intervalo = setInterval(() => {
      this.actualizarTiempoRestante(fin);

      if (Date.now() >= fin) {
        clearInterval(this.intervalo);
        this.tiempoRestanteTexto = '';
        this.error = false;
        this.mensajeError = '';
      }
    }, 1000);
  }

  private actualizarTiempoRestante(fin: number): void {
    const restante = fin - Date.now();
    const minutos = Math.floor(restante / 60000);
    const segundos = Math.floor((restante % 60000) / 1000);
    this.tiempoRestanteTexto = `Podés reenviar en ${minutos}:${segundos.toString().padStart(2, '0')}`;
  }

}
