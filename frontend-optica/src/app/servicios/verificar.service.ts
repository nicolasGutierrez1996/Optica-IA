import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '../model/api-response';

@Injectable({
  providedIn: 'root'
})
export class VerificarService {

  private apiUrl = '/api/verificador';
  private http:HttpClient = inject(HttpClient);


  verificarMail(token: string): Observable<ApiResponse<string>> {
    return this.http.get<ApiResponse<string>>(`${this.apiUrl}/verificar`, {
      params: { token }
    });
  }

  reenviarMailVerificacion(email: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(
      `${this.apiUrl}/reenviar-verificacion`,
      null,
      { params: { email } }
    );
  }


}
