import { Routes } from '@angular/router';
import { PublicLayoutComponent } from './layouts/public-layout/public-layout.component';
import { PanelLayoutComponent } from './layouts/panel-layout/panel-layout.component';
import { AuthGuard } from './auth/validaciones/auth.guard';

import { InicioComponent } from './public/inicio/inicio.component';
import { LoginComponent } from './auth/login/login.component';
import { RegistroComponent } from './auth/registro/registro.component';
import { VerificarComponent } from './auth/verificar/verificar.component';
import { VerificacionEnviadaComponent } from './auth/verificacion-enviada/verificacion-enviada.component';
import { RecuperarContraseniaComponent } from './auth/recuperar-contrasenia/recuperar-contrasenia.component';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', component: InicioComponent },
      { path: 'login', component: LoginComponent },
      { path: 'registrarse', component: RegistroComponent },
      { path: 'verificar', component: VerificarComponent },
      { path: 'verificacion-enviada', component: VerificacionEnviadaComponent },
      { path: 'recuperar-contrasenia', component: RecuperarContraseniaComponent }
    ]
  },
  {
    path: 'panel',
    component: PanelLayoutComponent,
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./panel/panel.routes').then(m => m.default)
  },
  { path: '**', redirectTo: '' }
];
