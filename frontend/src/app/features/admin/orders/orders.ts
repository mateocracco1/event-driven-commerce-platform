import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../AdminService/admin.service';
import { Order, OrderStatus, PaymentStatus } from '../../../core/models/order.model';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, MatIconModule, FormsModule],
  templateUrl: './orders.html',
  styleUrl: './orders.scss'
})
export class OrdersComponent implements OnInit {

  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  // Estado local
  orders: Order[] = [];
  currentPage = 0;
  pageSize = 15;
  totalElements = 0;
  totalPages = 0;
  isFirst = true;
  isLast = true;
  loading = false;

  // Filtros
  searchTerm = '';
  selectedStatus: OrderStatus | '' = '';
  selectedPaymentStatus: PaymentStatus | '' = '';
  startDate = '';
  endDate = '';
  sort = 'createdAt,desc';

  orderStatuses: OrderStatus[] = ['PENDING_PAYMENT', 'PAID', 'PROCESSING', 'DELIVERED', 'CANCELLED'];
  paymentStatuses: PaymentStatus[] = ['APPROVED', 'PENDING', 'REJECTED'];

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;

    this.adminService.getAllOrders({
      page: this.currentPage,
      size: this.pageSize,
      sort: this.sort,
      search: this.searchTerm,
      status: this.selectedStatus || undefined,
      paymentStatus: this.selectedPaymentStatus || undefined,
      startDate: this.startDate,
      endDate: this.endDate,
    }).subscribe({
      next: (page) => {
        this.orders = page.content;
        this.totalElements = page.totalElements;
        this.totalPages = page.totalPages;
        this.currentPage = page.number;
        this.isFirst = page.first;
        this.isLast = page.last;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.orders = [];
        this.totalElements = 0;
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onFilterChange() {
    this.currentPage = 0;
    this.loadOrders();
  }

  clearFilters() {
    this.searchTerm = '';
    this.selectedStatus = '';
    this.selectedPaymentStatus = '';
    this.startDate = '';
    this.endDate = '';
    this.sort = 'createdAt,desc';
    this.onFilterChange();
  }

  nextPage() {
    if (!this.isLast) {
      this.currentPage++;
      this.loadOrders();
    }
  }

  prevPage() {
    if (!this.isFirst) {
      this.currentPage--;
      this.loadOrders();
    }
  }

  updateStatus(orderId: number, event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    const newStatus = selectElement.value;

    this.adminService.updateOrderStatus(orderId, newStatus).subscribe({
      next: () => {
        this.loadOrders();
        this.cdr.detectChanges();
      },
      error: () => this.cdr.detectChanges()
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

  getOrderStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING_PAYMENT': return 'PENDIENTE';
      case 'PAID': return 'PAGADO';
      case 'PROCESSING': return 'EN PREPARACIÓN';
      case 'DELIVERED': return 'ENTREGADO';
      case 'CANCELLED': return 'CANCELADO';
      default: return status.toUpperCase();
    }
  }

  getPaymentStatusLabel(status?: string): string {
    switch (status) {
      case 'APPROVED': return 'Pagado';
      case 'PENDING': return 'Pendiente';
      case 'REJECTED': return 'Cancelado';
      default: return 'Sin definir';
    }
  }

  get startIndex(): number {
    if (this.totalElements === 0) return 0;
    return this.currentPage * this.pageSize + 1;
  }

  get endIndex(): number {
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalElements);
  }
}
