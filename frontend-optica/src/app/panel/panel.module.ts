import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { routes } from './panel.routes';
import { PanelComponent } from './panel.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    PanelComponent
  ]
})
export class PanelModule {}
