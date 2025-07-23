import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ApiResponse} from '../model/api-response';
import {Optica} from '../model/optica';
import {Observable} from 'rxjs';
import {CuponDescuento} from '../model/cupon-descuento';
import {CuponDTO} from '../model/CuponDTO';

@Injectable({
  providedIn: 'root'
})
export class CuponService {
  private apiUrl = '/api/cupones';
  private http: HttpClient=inject(HttpClient);

  generarCupon(id_optica:number):Observable<ApiResponse<CuponDescuento>>{
    return this.http.post<ApiResponse<CuponDescuento>>(`${this.apiUrl}/generar?idOptica=${id_optica}`, {});
  }
  buscarPorOptica(id_optica:number):Observable<ApiResponse<CuponDTO>>{
     return this.http.get<ApiResponse<CuponDTO>>(`${this.apiUrl}/por-optica/${id_optica}`);
  }
  }
