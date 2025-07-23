export interface Direccion {
  id?: number;
  provincia: string;
  localidad: string;
  calle: string;
  nroCalle: number;
  piso?: string;
  depto?: string;
  fecha_creacion?: string;
  fecha_ultima_actualizacion?: string;
}
