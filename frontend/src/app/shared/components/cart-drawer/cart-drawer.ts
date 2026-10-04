import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';

import { CartService } from '../../../services/cart.service';
import { OrderService } from '../../../services/order.service';

@Component({
  selector: 'app-cart-drawer',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatListModule,
    MatDividerModule
  ],
  templateUrl: './cart-drawer.html',
  styleUrl: './cart-drawer.scss'
})
export class CartDrawerComponent {
  public cartService = inject(CartService);
  private orderService = inject(OrderService);
  private snackBar = inject(MatSnackBar);

  isProcessing = signal(false);

  closeDrawer() {
    this.cartService.close();
  }

  removeItem(productId: number): void {
    if (productId) {
      this.cartService.removeItem(productId);
    }
  }

  deleteItem(productId: number): void {
    if (productId) {
      this.cartService.deleteItem(productId);
    }
  }

  checkout(): void {
    const items = this.cartService.items();
    if (items.length === 0) return;

    this.isProcessing.set(true);

    const request = {
      items: items.map(item => ({
        productId: item.product.id!,
        quantity: item.quantity
      }))
    };

    this.orderService.checkout(request).subscribe({
      next: (res) => {
        window.location.href = res.paymentUrl;
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Error al procesar el pago. Intente nuevamente.', 'Cerrar', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        this.isProcessing.set(false);
      }
    });
  }
}
