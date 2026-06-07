import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  {
    // Esta es la ruta padre del panel de admin (el layout con el sidebar)
    path: '',
    loadComponent: () => import('./layout/admin-layout/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      // Si entran a /admin a secas, los redirige automáticamente a la lista de usuarios
      { path: '', redirectTo: 'users', pathMatch: 'full' },
      
      // Sección de Usuarios
      { 
        path: 'users', 
        loadComponent: () => import('./users/user-list/user-list.component').then(m => m.UserListComponent) 
      },
      
      // NUEVA: Sección de Lista de Productos
      { 
        path: 'products', 
        loadComponent: () => import('./products/product-list/product-list').then(m => m.ProductListComponent) 
      },
      
      { 
      path: 'categories', 
      loadComponent: () => import('./categories/category-list/category-list').then(m => m.CategoryList) 
      }
    ]
  }
];