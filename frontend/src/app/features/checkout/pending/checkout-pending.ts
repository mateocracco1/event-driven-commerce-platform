import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { CartService } from '../../../services/cart.service';

@Component({
  selector: 'app-checkout-pending',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './checkout-pending.html',
  styleUrl: './checkout-pending.scss',
})
export class CheckoutPendingComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cartService = inject(CartService);

  orderId: string | null = null;
  paymentId: string | null = null;

  // Tiempos de acreditación según el medio de pago de Mercado Pago
  readonly paymentTimes = [
    { icon: 'credit_card', label: 'Tarjeta de crédito', time: 'Inmediato (sujeto a banco)' },
    { icon: 'account_balance', label: 'Transferencia bancaria', time: 'Hasta 72 horas hábiles' },
    { icon: 'payments', label: 'Rapipago / Pago Fácil', time: 'Hasta 2 días hábiles' },
    { icon: 'receipt', label: 'Boleto Bancario', time: 'Hasta 3 días hábiles' },
  ];

  ngOnInit(): void {
    this.paymentId = this.route.snapshot.queryParamMap.get('payment_id');
    this.orderId = this.route.snapshot.queryParamMap.get('order_id')
      ?? this.route.snapshot.queryParamMap.get('merchant_order_id');

    // ✅ Vaciamos el carrito: el pago está en proceso, no debe reintentarse
    this.cartService.clearCart();

  }

  goToMyOrders(): void {
    this.router.navigate(['/my-orders']);
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }
}
