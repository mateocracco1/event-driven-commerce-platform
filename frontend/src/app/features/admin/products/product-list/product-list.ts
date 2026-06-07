import { Component, inject, OnInit, signal } from '@angular/core';
import { Category, Product } from '../../../../core/models/product.model'; // Ajustá los puntos (../) según tu estructura para llegar a core
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../../AdminService/admin.service';
import { ProductFormComponent } from '../product-form/product-form';
import { StockAdjustmentFormComponent } from '../stock-adjustment-form/stock-adjustment-form';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, RouterModule, MatIconModule , ProductFormComponent , StockAdjustmentFormComponent],
  templateUrl: './product-list.html',
  styleUrl: './product-list.scss',
})
export class ProductListComponent implements OnInit{


  private adminService = inject(AdminService);
  // Definimos el signal que va a contener el array de productos (empieza vacío)
  products = signal<Product[]>([]);

  // Estado para controlar la visibilidad del formulario/modal
  showForm = signal<boolean>(false);
  // Guardamos el producto seleccionado por si en el futuro tocás "Editar"
  selectedProduct = signal<Product | null>(null);
  
  categories = signal<Category[]>([]); // Cargaremos las categorías para el select del filtro

  // === SIGNALS PARA FILTROS ===
  filterQuery = signal<string>('');
  filterCategory = signal<string>('');
  filterOrder = signal<string>('asc');

  //para stock
  showStockForm = signal<boolean>(false);

  ngOnInit(): void {
  this.loadInitialData(); // <-- ¡Esta línea tiene que estar sí o sí!
  }

  loadInitialData(): void {
    this.loadProducts();
    
    // Traemos categorías para poblar el dropdown de filtros
    this.adminService.getAllCategories().subscribe({
      next: (data) => this.categories.set(data),
      error: (err) => console.error('Error al cargar categorías para filtros:', err)
    });
  }


// Modificamos para que use el endpoint avanzado /search enviando el estado de los signals
  loadProducts(): void {
    const query = this.filterQuery();
    const catId = this.filterCategory() ? Number(this.filterCategory()) : undefined;
    const order = this.filterOrder();

    this.adminService.searchProducts(query, catId, order).subscribe({
      next: (data) => this.products.set(data),
      error: (err) => console.error('Error al filtrar catálogo de productos:', err)
    });
  }


  // === CAPTURA DE EVENTOS DE USUARIO ===
  onQueryChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.filterQuery.set(input.value);
    this.loadProducts(); // Dispara la búsqueda al escribir
  }

  onCategoryChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.filterCategory.set(select.value);
    this.loadProducts(); // Filtra al cambiar de opción
  }

  onOrderChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.filterOrder.set(select.value);
    this.loadProducts(); // Ordena al cambiar de opción
  }

  clearFilters(): void {
    this.filterQuery.set('');
    this.filterCategory.set('');
    this.filterOrder.set('asc');
    this.loadProducts(); // Resetea la grilla a su estado inicial
  }

  // Al tocar "Nuevo Producto"
  openCreateForm(): void {
    this.selectedProduct.set(null); // Modo creación
    this.showForm.set(true);
  }

  // Al tocar el botón de editar de la tabla
  openEditForm(product: Product): void {
    this.selectedProduct.set(product); // Modo edición
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.selectedProduct.set(null);
  }

  // Se ejecuta cuando el formulario nos avise que guardó con éxito en el backend
  onProductSaved(): void {
    this.closeForm();
    this.loadProducts(); // Recargamos la lista para ver los cambios reflejados
  }

  // Método para borrar un producto (aprovechamos y lo dejamos listo)
  deleteProduct(id: number): void {
    if (confirm('¿Estás seguro de que querés eliminar este producto?')) {
      this.adminService.deleteProduct(id).subscribe({
        next: () => {
          // Actualizamos el signal filtrando el producto eliminado para que impacte en la vista al toque
          this.products.update(prevProducts => prevProducts.filter(p => p.id !== id));
        },
        error: (err) => {
          console.error('Error al eliminar el producto:', err);
        }
      });
    }
  }

  //metodos para gestion de stock

  openStockForm(product: Product): void {
  this.selectedProduct.set(product);
  this.showStockForm.set(true);
}

closeStockForm(): void {
  this.showStockForm.set(false);
  this.selectedProduct.set(null);
}

onStockSaved(): void {
  this.closeStockForm();
  this.loadProducts(); // Recarga la tabla con el stock actualizado
}


}
