import {Optica} from './optica';

export interface CuponDescuento {
  id?: number;
  codigo: string;
  opticaEmisora?: Optica;
  opticasReferidas?: Optica[];
  fechaCreacion?: string;
  beneficioOtorgado?: boolean;
}
