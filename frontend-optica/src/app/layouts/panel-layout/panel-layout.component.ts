import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-panel-layout',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './panel-layout.component.html',
})
export class PanelLayoutComponent {}
