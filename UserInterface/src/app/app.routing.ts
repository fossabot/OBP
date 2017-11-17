import { Routes, RouterModule } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';
import { ViewerPageComponent } from './viewerpage.component';
//import { UserRoleRequestsComponent } from './pages/userroles/userroles.component';

export const routes: Routes = [
  { path: '', redirectTo: 'pages', pathMatch: 'full' },
  { path: '**', redirectTo: 'pages/dashboard' },
  { path: 'viewerpage', component: ViewerPageComponent },
];

export const routing: ModuleWithProviders = RouterModule.forRoot(routes, { useHash: true });
