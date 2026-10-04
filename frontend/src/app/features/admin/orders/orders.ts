import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../AdminService/admin.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, MatIconModule, FormsModule],
  templateUrl: './orders.html',
  styleUrl: './orders.scss'
})
export class OrdersComponent implements OnInit {

  private adminService = inject(AdminService);
  orders = signal<any[]>([]);

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
    this.adminService.getAllOrders().subscribe(data => {
      // Ordenar más recientes primero
      this.orders.set(data.sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()));
    });
  }

  updateStatus(orderId: number, event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    const newStatus = selectElement.value;

    this.adminService.updateOrderStatus(orderId, newStatus).subscribe(() => {
      this.loadOrders();
    });
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING_PAYMENT': return 'Pendiente';
      case 'PAID': return 'Pagado';
      case 'PROCESSING': return 'En Proceso';
      case 'DELIVERED': return 'Enviado/Entregado';
      case 'CANCELLED': return 'Cancelado';
      default: return status;
    }
  }
}
