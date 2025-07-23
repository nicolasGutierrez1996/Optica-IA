import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Contacto} from '../model/contacto';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmailService {


  private apiUrl = '/api/email';
  private http:HttpClient=inject(HttpClient);


  enviarContacto(contacto: Contacto): Observable<any> {
    return this.http.post(`${this.apiUrl}/contacto`, contacto);
  }
}
