// ============================================================
// MODELOS DEL MÓDULO DE ÓRDENES / PAGOS
// Pasarela: Mercado Pago Checkout Pro (redirección externa)
// ============================================================

/**
 * Estados posibles de una orden a lo largo de su ciclo de vida.
 * Definidos por el backend (Spring Boot).
 */
export type OrderStatus = 'PENDING_PAYMENT' | 'PAID' | 'CANCELLED' | 'DELIVERED';

// ------------------------------------------------------------
// Ítem dentro de una orden (para lectura/detalle)
// ------------------------------------------------------------
export interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number; // unitPrice * quantity (puede calcularse en el front también)
}

// ------------------------------------------------------------
// Orden completa (respuesta del GET /api/orders y /api/orders/{id})
// ------------------------------------------------------------
export interface Order {
  id: number;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;   // ISO 8601 string, ej: "2026-10-03T18:00:00"
  updatedAt?: string;
  items: OrderItem[];
  // Datos del cliente (útil en la vista Admin)
  customerName?: string;
  customerEmail?: string;
}

// ------------------------------------------------------------
// REQUEST: lo que mandamos al POST /api/orders/checkout
// ------------------------------------------------------------
export interface CheckoutItem {
  productId: number;
  quantity: number;
}

export interface CheckoutRequest {
  items: CheckoutItem[];
}

// ------------------------------------------------------------
// RESPONSE: lo que devuelve el POST /api/orders/checkout
// ------------------------------------------------------------
export interface CheckoutResponse {
  orderId: number;
  paymentUrl: string;   // URL de Mercado Pago → usamos window.location.href
  status: OrderStatus;
  totalAmount: number;
}

// ------------------------------------------------------------
// REQUEST: Admin actualiza el estado de una orden
// PATCH /api/orders/{id}/status
// ------------------------------------------------------------
export interface UpdateOrderStatusRequest {
  status: OrderStatus;
}
