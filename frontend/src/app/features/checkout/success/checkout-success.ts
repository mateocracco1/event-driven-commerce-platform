import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-checkout-success',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './checkout-success.html',
  styleUrl: './checkout-success.scss',
})
export class CheckoutSuccessComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cartService = inject(CartService);

  orderId: string | null = null;
  paymentId: string | null = null;

  ngOnInit(): void {
    // Mercado Pago devuelve query params: ?payment_id=xxx&order_id=xxx
    this.paymentId = this.route.snapshot.queryParamMap.get('payment_id');
    this.orderId = this.route.snapshot.queryParamMap.get('order_id') 
                ?? this.route.snapshot.queryParamMap.get('merchant_order_id');

    // Vaciar el carrito al llegar aquí
    this.cartService.clearCart();

  }

  goToMyOrders(): void {
    this.router.navigate(['/my-orders']);
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }
}
