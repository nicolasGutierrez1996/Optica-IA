import {TipoSuscripcion} from './tipo-suscripcion';
import {Usuario} from './usuario';


export interface Suscripcion {
  id: number;
  usuario: Usuario;
  tipoSuscripcion: TipoSuscripcion;
  fechaInicio: string;
  fechaVencimiento?: string;
  montoFinal?: number;
  activa: boolean;
  estado: 'PENDIENTE_PAGO' | 'APROBADA' | 'RECHAZADA';
  descuentoAplicado?: number;
}
