import { Routes, RouterModule }  from '@angular/router';
import { ViewerPageComponent } from './viewerpage.component';
import { ModuleWithProviders } from '@angular/core';
// noinspection TypeScriptValidateTypes

// export function loadChildren(path) { return System.import(path); };

export const routes: Routes = [
  {
    path: "viewerpage",
    component: ViewerPageComponent,
    children: [
      { path: 'objects', loadChildren: './pages/objects/objects.module#ObjectsModule' },
    ]
  }
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
