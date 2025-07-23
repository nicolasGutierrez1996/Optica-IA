import {Optica} from './optica';

export interface Usuario {
  id?: number;
  username: string;
  nombre: string;
  apellido: string;
  dni?: string;
  password: string;
  email: string;
  optica?: Optica | null;
  fecha_creacion?: string;
  fecha_ultima_actualizacion?: string;
  token?: string;
  tokenExpiracion?: string;
}
