import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';

import { Product, Category } from '../../core/models/product.model';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class HomeComponent implements OnInit {
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  private snackBar = inject(MatSnackBar);

  // Estado
  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  loading = signal(true);
  error = signal(false);

  // Filtros
  searchQuery = '';
  selectedCategory: number | undefined = undefined;
  priceOrder = 'asc';

  // Cantidad seleccionada por producto (productId → qty)
  quantities: Record<number, number> = {};

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading.set(true);
    this.error.set(false);

    this.productService.search(
      this.searchQuery || undefined,
      this.selectedCategory,
      this.priceOrder
    ).subscribe({
      next: (data) => {
        this.products.set(data);
        // Inicializa quantities en 1 para cada producto
        data.forEach(p => {
          if (p.id && !this.quantities[p.id]) this.quantities[p.id] = 1;
        });
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      }
    });
  }

  loadCategories(): void {
    this.productService.getCategories().subscribe({
      next: (data) => this.categories.set(data),
      error: () => {} // silencioso, los filtros son opcionales
    });
  }

  // Control de cantidad por producto
  getQty(productId: number): number {
    return this.quantities[productId] ?? 1;
  }

  increaseQty(productId: number): void {
    this.quantities[productId] = this.getQty(productId) + 1;
  }

  decreaseQty(productId: number): void {
    const current = this.getQty(productId);
    if (current > 1) this.quantities[productId] = current - 1;
  }

  // Agregar al carrito
  addToCart(product: Product): void {
    const qty = product.id ? this.getQty(product.id) : 1;
    this.cartService.addItem(product, qty);
    this.snackBar.open(
      `✓ ${product.name} agregado al carrito`,
      'Ver carrito',
      { duration: 3000, panelClass: ['snack-success'] }
    );
    // Reset de cantidad
    if (product.id) this.quantities[product.id] = 1;
  }

  // Filtros
  onSearchChange(): void {
    this.loadProducts();
  }

  onCategoryChange(): void {
    this.loadProducts();
  }

  onOrderChange(): void {
    this.loadProducts();
  }

  clearFilters(): void {
    this.searchQuery = '';
    this.selectedCategory = undefined;
    this.priceOrder = 'asc';
    this.loadProducts();
  }

  // Fallback de imagen rota
  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src =
      'https://material.angular.io/assets/img/examples/shiba2.jpg';
  }
}
