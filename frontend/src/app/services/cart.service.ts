import { Injectable, signal, computed } from '@angular/core';
import { Product } from '../core/models/product.model';

export interface CartItem {
  product: Product;
  quantity: number;
}

@Injectable({
  providedIn: 'root',
})
export class CartService {
  // Estado reactivo del carrito usando Signals
  private _items = signal<CartItem[]>(this.loadFromStorage());

  // Expuesto como readonly
  readonly items = this._items.asReadonly();

  // Total de ítems (suma de quantities) — para el badge del navbar
  readonly itemCount = computed(() =>
    this._items().reduce((sum, item) => sum + item.quantity, 0)
  );

  // Total monetario
  readonly total = computed(() =>
    this._items().reduce((sum, item) => sum + item.product.price * item.quantity, 0)
  );

  // ----------------------------------------------------------
  // Agregar producto (o incrementar si ya existe)
  // ----------------------------------------------------------
  addItem(product: Product, quantity: number = 1): void {
    this._items.update(items => {
      const existing = items.find(i => i.product.id === product.id);
      if (existing) {
        return items.map(i =>
          i.product.id === product.id
            ? { ...i, quantity: i.quantity + quantity }
            : i
        );
      }
      return [...items, { product, quantity }];
    });
    this.saveToStorage();
  }

  // ----------------------------------------------------------
  // Quitar una unidad (o eliminar si llega a 0)
  // ----------------------------------------------------------
  removeItem(productId: number): void {
    this._items.update(items =>
      items
        .map(i => i.product.id === productId ? { ...i, quantity: i.quantity - 1 } : i)
        .filter(i => i.quantity > 0)
    );
    this.saveToStorage();
  }

  // ----------------------------------------------------------
  // Eliminar producto completo del carrito
  // ----------------------------------------------------------
  deleteItem(productId: number): void {
    this._items.update(items => items.filter(i => i.product.id !== productId));
    this.saveToStorage();
  }

  // ----------------------------------------------------------
  // Vaciar carrito (llamar en checkout success y pending)
  // ----------------------------------------------------------
  clearCart(): void {
    this._items.set([]);
    localStorage.removeItem('cart');
  }

  // ----------------------------------------------------------
  // Persistencia en localStorage
  // ----------------------------------------------------------
  private saveToStorage(): void {
    localStorage.setItem('cart', JSON.stringify(this._items()));
  }

  private loadFromStorage(): CartItem[] {
    try {
      const raw = localStorage.getItem('cart');
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  }
}
