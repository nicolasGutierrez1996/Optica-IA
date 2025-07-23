export interface TipoSuscripcion {
  id: number;
  nombre: string;
  descripcion?: string;
  valor: number;
  activo: boolean;
}
