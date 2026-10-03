import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Product, Category } from '../core/models/product.model';

/**
 * Servicio público de productos — no requiere autenticación.
 * Para operaciones de admin (crear, editar, borrar) usar AdminService.
 */
@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private http = inject(HttpClient);
  private productApiUrl = 'http://localhost:8080/api/products';
  private categoryApiUrl = 'http://localhost:8080/api/categories';

  // Catálogo público (sin credenciales)
  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.productApiUrl);
  }

  // Búsqueda/filtrado público
  search(query?: string, categoryId?: number, priceOrder: string = 'asc'): Observable<Product[]> {
    let params = new HttpParams();
    if (query) params = params.set('query', query);
    if (categoryId) params = params.set('categoryId', categoryId.toString());
    if (priceOrder) params = params.set('priceOrder', priceOrder);
    return this.http.get<Product[]>(`${this.productApiUrl}/search`, { params });
  }

  // Categorías para el filtro
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryApiUrl);
  }
}
