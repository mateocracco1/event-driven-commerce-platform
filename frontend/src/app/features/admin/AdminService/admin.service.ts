import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Product, Category } from '../../../core/models/product.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/users';
  private productApiUrl = 'http://localhost:8080/api/products'; 
  private categoryApiUrl = 'http://localhost:8080/api/categories';


  // Obtener todos los usuarios (Solo para ADMIN)
  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }

  // Eliminar un usuario
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  // === GESTIÓN DE PRODUCTOS ===
  
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.productApiUrl, { withCredentials: true });
  }

  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.productApiUrl, product, { withCredentials: true });
  }

  updateProduct(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.productApiUrl}/${id}`, product, { withCredentials: true });
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.productApiUrl}/${id}`, { withCredentials: true });
  }

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryApiUrl, { withCredentials: true });
  }

  // === GESTIÓN DE CATEGORÍAS ===

  createCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(this.categoryApiUrl, category, { withCredentials: true });
  }

  updateCategory(id: number, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.categoryApiUrl}/${id}`, category, { withCredentials: true });
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.categoryApiUrl}/${id}`, { withCredentials: true });
  }

}