import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../AdminService/admin.service';
import { Product } from '../../../../core/models/product.model';

@Component({
  selector: 'app-stock-adjustment-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './stock-adjustment-form.html',
  styleUrls: ['./stock-adjustment-form.scss']
})
export class StockAdjustmentFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminService);

  @Input() product: Product | null = null;
  @Output() onClose = new EventEmitter<void>();
  @Output() onSave = new EventEmitter<void>();

  stockForm!: FormGroup;

ngOnInit(): void {
  this.stockForm = this.fb.group({
    // Corregido: Solo required. Podés sumar Validators.min() si querés, 
    // pero como el back acepta negativos para restar stock, lo dejamos libre.
    quantity: [0, [Validators.required]], 
    reason: ['MANUAL_ADJUSTMENT', [Validators.required]]
  });
}
  onSubmit(): void {
    if (this.stockForm.invalid || !this.product?.id) return;

    const { quantity, reason } = this.stockForm.value;

    this.adminService.adjustStock(this.product.id, quantity, reason).subscribe({
      next: () => {
        alert('Movimiento de stock registrado con éxito');
        this.onSave.emit();
      },
      error: (err) => {
        console.error('Error al ajustar stock:', err);
        alert('Hubo un error al registrar el movimiento');
      }
    });
  }
}