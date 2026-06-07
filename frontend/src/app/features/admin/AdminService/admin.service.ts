
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Product, Category } from '../../../core/models/product.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/users'; // Ajusta a tu endpoint de Spring
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
  
  // Obtener todos los productos del catálogo
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.productApiUrl, { withCredentials: true });
  }

  // Crear un nuevo producto (Cumple con tu User Story de Admin)
  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.productApiUrl, product, { withCredentials: true });
  }

  // Eliminar un producto por su ID
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.productApiUrl}/${id}`, { withCredentials: true });
  }

  // Obtener las categorías para cargar el elemento <select> del formulario
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryApiUrl, { withCredentials: true });
  }

}