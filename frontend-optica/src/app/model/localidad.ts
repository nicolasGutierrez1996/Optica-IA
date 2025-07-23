import {Provincia} from './provincia';


export interface Localidad {
  id?: number;
  nombre: string;
  provincia: Provincia;
}
