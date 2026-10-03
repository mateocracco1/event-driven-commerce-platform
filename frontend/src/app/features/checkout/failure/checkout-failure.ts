import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-checkout-failure',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './checkout-failure.html',
  styleUrl: './checkout-failure.scss',
})
export class CheckoutFailureComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  paymentId: string | null = null;
  errorMessage: string = 'Ocurrió un problema al procesar tu pago.';

  ngOnInit(): void {
    this.paymentId = this.route.snapshot.queryParamMap.get('payment_id');

    // Mercado Pago puede devolver un código de error
    const statusDetail = this.route.snapshot.queryParamMap.get('status_detail');
    if (statusDetail) {
      this.errorMessage = this.resolveErrorMessage(statusDetail);
    }

    // ❌ NO vaciamos el carrito → el usuario puede reintentar
  }

  private resolveErrorMessage(detail: string): string {
    const messages: Record<string, string> = {
      'cc_rejected_insufficient_amount': 'Fondos insuficientes en tu tarjeta.',
      'cc_rejected_bad_filled_card_number': 'El número de tarjeta es incorrecto.',
      'cc_rejected_bad_filled_date': 'La fecha de vencimiento es incorrecta.',
      'cc_rejected_bad_filled_security_code': 'El código de seguridad es incorrecto.',
      'cc_rejected_call_for_authorize': 'Tu banco solicitó autorización. Por favor contáctalos.',
      'rejected_by_bank': 'El pago fue rechazado por el banco.',
    };
    return messages[detail] ?? 'Ocurrió un problema al procesar tu pago.';
  }

  retryCheckout(): void {
    // Vuelve al carrito para que el usuario reintente
    this.router.navigate(['/cart']);
  }

  goToHome(): void {
    this.router.navigate(['/home']);
  }
}
