import { Routes } from '@angular/router';
import { InicioComponent } from './public/inicio/inicio.component';
import { LoginComponent } from './auth/login/login.component';
import { RegistroComponent } from './auth/registro/registro.component';

export const routes: Routes = [
  { path: '', component: InicioComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registrarse', component: RegistroComponent },
  {
    path: 'panel',
    loadChildren: () =>
      import('./panel/panel.module').then(m => m.PanelModule),
  },
  { path: '**', redirectTo: '' },
];
