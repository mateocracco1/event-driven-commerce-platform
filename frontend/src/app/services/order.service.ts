import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  CheckoutRequest,
  CheckoutResponse,
  Order,
  UpdateOrderStatusRequest,
} from '../core/models/order.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/orders';

  // ----------------------------------------------------------
  // CLIENTE: Iniciar proceso de pago
  // POST /api/orders/checkout
  // Recibe el carrito, devuelve la URL de Mercado Pago
  // ----------------------------------------------------------
  checkout(request: CheckoutRequest): Observable<CheckoutResponse> {
    return this.http.post<CheckoutResponse>(`${this.apiUrl}/checkout`, request, {
      withCredentials: true,
    });
  }

  // ----------------------------------------------------------
  // CLIENTE: Ver historial de mis órdenes
  // GET /api/orders/my-orders
  // ----------------------------------------------------------
  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/my-orders`, {
      withCredentials: true,
    });
  }

  // ----------------------------------------------------------
  // CLIENTE / ADMIN: Ver detalle de una orden específica
  // GET /api/orders/{id}
  // ----------------------------------------------------------
  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`, {
      withCredentials: true,
    });
  }

  // ----------------------------------------------------------
  // ADMIN: Ver todas las órdenes del sistema
  // GET /api/orders
  // ----------------------------------------------------------
  getAllOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl, {
      withCredentials: true,
    });
  }

  // ----------------------------------------------------------
  // ADMIN: Actualizar estado de una orden (ej: a DELIVERED)
  // PATCH /api/orders/{id}/status
  // ----------------------------------------------------------
  updateOrderStatus(id: number, request: UpdateOrderStatusRequest): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/${id}/status`, request, {
      withCredentials: true,
    });
  }

  // ----------------------------------------------------------
  // CLIENTE / ADMIN: Cancelar una orden
  // Solo permitido si status === 'PENDING_PAYMENT'
  // PATCH /api/orders/{id}/cancel
  // ----------------------------------------------------------
  cancelOrder(id: number): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/${id}/cancel`, {}, {
      withCredentials: true,
    });
  }
}
