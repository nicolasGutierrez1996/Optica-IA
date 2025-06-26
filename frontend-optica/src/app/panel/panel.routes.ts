import { Routes } from '@angular/router';
import { PanelComponent } from './panel.component';

export const routes: Routes = [
  {
    path: '',
    component: PanelComponent,
    children: [
      // Aquí agregás las subrutas como:
      // { path: 'dashboard', component: DashboardComponent },
      // { path: 'perfil', component: PerfilComponent },
    ]
  }
];
