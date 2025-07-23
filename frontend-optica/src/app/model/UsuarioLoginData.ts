import {Optica} from './optica';

export interface UsuarioLoginData {
  token: string;
  usuario: {
    id: number;
    username: string;
    optica: Optica;
  };
}
