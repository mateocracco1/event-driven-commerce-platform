import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './layout/admin-layout/admin-layout.component';
import { UserListComponent } from './users/user-list/user-list.component';


export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent, // El cascarón (Sidebar + Header)
    children: [
      { path: 'users', component: UserListComponent },
      { path: '', redirectTo: 'users', pathMatch: 'full' } // Por defecto a usuarios
    ]
  }
];