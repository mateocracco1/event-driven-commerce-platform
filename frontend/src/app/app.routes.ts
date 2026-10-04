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

  // ── Mis Órdenes ──────────────────────────
  {
    path: 'my-orders',
    loadComponent: () => import('./features/my-orders/my-orders').then(m => m.MyOrdersComponent)
  },

  // ── Páginas de feedback de Mercado Pago ──────────────────────────
  {
    path: 'checkout/success',
    loadComponent: () => import('./features/checkout/success/checkout-success').then(m => m.CheckoutSuccessComponent)
  },
  {
    path: 'checkout/failure',
    loadComponent: () => import('./features/checkout/failure/checkout-failure').then(m => m.CheckoutFailureComponent)
  },
  {
    path: 'checkout/pending',
    loadComponent: () => import('./features/checkout/pending/checkout-pending').then(m => m.CheckoutPendingComponent)
  },

  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
  
  
];