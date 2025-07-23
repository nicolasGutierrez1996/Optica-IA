import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Localidad} from '../model/localidad';
import {Observable} from 'rxjs';
import {Provincia} from '../model/provincia';

@Injectable({
  providedIn: 'root'
})
export class ProvinciaService {
  private baseUrl: string='/api/provincia';
  private http: HttpClient=inject(HttpClient);


  listarProvincias():Observable<Provincia[]>{
    return this.http.get<Provincia[]>(`${this.baseUrl}`);
  }

  obtenerLocalidades(id:number):Observable<Localidad[]>{
    return this.http.get<Localidad[]>(`${this.baseUrl}/${id}/localidades`);
  }

}
