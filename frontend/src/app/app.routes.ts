import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { HomeComponent } from './pages/home/home';
import { RegisterComponent } from './features/register/register';
import { ForgotPasswordComponent } from './features/auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password.component';
import { adminGuard } from './core/guards/admin-guard';
import { ResellerRegisterComponet } from './features/reseller-register/reseller-register';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  
  { 
    path: 'admin', 
    canActivate: [adminGuard], // <-- ESTO es la llave
    loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES) 
  },
  {
    path: 'admin',
    loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES),
    // Aquí luego pondremos el adminGuard para seguridad
  },
  { path: 'home', component: HomeComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reseller-register', component: ResellerRegisterComponet },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
  
  
];