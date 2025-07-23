import {Localidad} from './localidad';

export interface Provincia {
  id?: number;
  nombre: string;
  localidades?: Localidad[]; // opcional si no siempre viene del backend
}
