import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Contacto} from '../model/contacto';
import {Observable} from 'rxjs';
import {ApiResponse} from '../model/api-response';
import {Usuario} from '../model/usuario';
import {UsuarioLoginData} from '../model/UsuarioLoginData';
import {Suscripcion} from '../model/suscripcion';
import {PerfilDTO} from '../model/perfil-dto';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private apiUrl = '/api/usuarios';
  private http:HttpClient = inject(HttpClient);

    autogenerarUserName(nombre: string, apellido: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/autogenerarUserName`, {
      nombre,
      apellido
    });
  }

  existeUserName(username: string): Observable<ApiResponse<boolean>> {
    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/existeUserName/${username}`)
  }

  existeEmail(email: string): Observable<ApiResponse<boolean>> {
    return this.http.get<ApiResponse<boolean>>(`${this.apiUrl}/existeEmail/${email}`)
  }

  crearUsuario(usuario: Usuario) {
    return this.http.post<ApiResponse<Usuario>>(`${this.apiUrl}`, usuario);
  }

  esUsuarioVerificado(username: string): Observable<ApiResponse<void>> {
      return this.http.get<ApiResponse<void>>(`${this.apiUrl}/usuarioVerificado/${username}`)
  }

  esContraseniaCorrecta(username: string,contrasenia:string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/verificarContrasenia`,{
      username:username,
      contrasenia:contrasenia
    });
  }

  login(username: string): Observable<ApiResponse<UsuarioLoginData>> {
    return this.http.post<ApiResponse<UsuarioLoginData>>(`${this.apiUrl}/login`, {
      username
    });
  }

  recuperarContrasenia(email: string): Observable<ApiResponse<void>> {
      return this.http.put<ApiResponse<void>>(`${this.apiUrl}/recuperar/${email}`,{});
  }

  verificarToken(email: string,token:string): Observable<ApiResponse<void>> {
      return this.http.get<ApiResponse<void>>(`${this.apiUrl}/verificar-token/${email}/${token}`);
  }

  actualizarClave(email:string,clave:string): Observable<ApiResponse<void>> {
      return this.http.put<ApiResponse<void>> (`${this.apiUrl}/actualizar-clave`,{
        email:email,
        nuevaClave:clave
      });
  }

  suscribirse(payload: {
    usuarioId: number;
    dniUsuario: string;
    opticaId: number;
    tipoSuscripcionId: number;
    cupon?: string;
  }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/suscribirse`, payload);
  }


  buscarPorUsuario(idUsuario: number): Observable<Suscripcion> {
    return this.http.get<Suscripcion>(`${this.apiUrl}/suscripcion/${idUsuario}`);
  }
  obtenerMiPerfil(): Observable<ApiResponse<Usuario>> {
    return this.http.get<ApiResponse<Usuario>>(`${this.apiUrl}/mi-perfil`);
  }

  obtenerDniPorId(id:number): Observable<ApiResponse<string>> {
      return this.http.get<ApiResponse<string>>(`${this.apiUrl}/${id}/dni`);
  }

  actualizarPerfil(id:number,perfil:PerfilDTO): Observable<ApiResponse<PerfilDTO>>{
      return this.http.put<ApiResponse<PerfilDTO>>(`${this.apiUrl}/perfil/${id}`,perfil);
  }

  validarContrasenia(id:number,contrasenia:string): Observable<ApiResponse<void>> {
      return this.http.post<ApiResponse<void>>(`${this.apiUrl}/confirmar-password`,{
        usuarioId:id,
        password:contrasenia
      })
  }

}
