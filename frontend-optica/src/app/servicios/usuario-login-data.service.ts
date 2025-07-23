import { Injectable } from '@angular/core';
import {Optica} from '../model/optica';

@Injectable({
  providedIn: 'root'
})
export class UsuarioLoginDataService {

  private obtenerPayload(): any | null {
    const token = localStorage.getItem('jwt');
    if (!token) return null;

    try {
      const payloadBase64 = token.split('.')[1];
      return JSON.parse(atob(payloadBase64));
    } catch (e) {
      console.error('Error al decodificar el token JWT', e);
      return null;
    }
  }

  getUsuarioId(): number | null {
    return this.obtenerPayload()?.id || null;
  }



  getOptica(): Optica | null {
    return this.obtenerPayload()?.optica || null;
  }



}
