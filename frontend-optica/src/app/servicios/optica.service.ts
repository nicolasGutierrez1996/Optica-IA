import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Optica} from '../model/optica';
import {ApiResponse} from '../model/api-response';

@Injectable({
  providedIn: 'root'
})
export class OpticaService {
   private baseUrl = '/api/opticas';
   private http:HttpClient=inject(HttpClient);


  subirLogo(archivo: File): Observable<ApiResponse> {
    const formData = new FormData();
    formData.append('archivo', archivo);

    return this.http.post<ApiResponse>(`${this.baseUrl}/upload-logo`, formData);
  }

  guardar(optica: Optica): Observable<ApiResponse<Optica>> {
    return this.http.post<ApiResponse<Optica>>(`${this.baseUrl}`, optica);
  }

  editar(id_optica: number, optica_editada: Optica): Observable<ApiResponse<Optica>> {
    return this.http.put<ApiResponse<Optica>>(`${this.baseUrl}/${id_optica}`, optica_editada);
  }
  eliminar(id_optica:number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/${id_optica}`);
  }

}
