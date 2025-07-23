import { Routes } from '@angular/router';
import { PanelComponent } from './panel.component';

const routes: Routes = [
  {
    path: '',
    component: PanelComponent,
    children: [
      // Ejemplos de subrutas internas del panel:
      // { path: 'dashboard', component: DashboardComponent },
      // { path: 'perfil', component: PerfilComponent },
    ]
  }
];

export default routes;
