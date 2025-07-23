import {Direccion} from './direccion';
import {CuponDescuento} from './cupon-descuento';

export interface Optica {
  id?: number;
  nombre: string;
  logoUrl?: string;
  direccion: Direccion;
  telefono: string;
  email: string;
  descripcion?: string;
  activo?: boolean;
  fecha_creacion?: string;
  fecha_ultima_actualizacion?: string;
  cuponUtilizado?: CuponDescuento;
}
