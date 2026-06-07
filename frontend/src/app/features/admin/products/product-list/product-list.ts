import { Component, inject, OnInit, signal } from '@angular/core';
import { Product } from '../../../../core/models/product.model'; // Ajustá los puntos (../) según tu estructura para llegar a core
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../../AdminService/admin.service';
import { ProductFormComponent } from '../product-form/product-form';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, RouterModule, MatIconModule , ProductFormComponent],
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


  ngOnInit(): void {
    this.loadProducts();
  }

// Método para ir a buscar los productos al backend de Spring
  loadProducts(): void {
    this.adminService.getAllProducts().subscribe({
      next: (data) => {
        this.products.set(data); // Guardamos los productos en el signal
      },
      error: (err) => {
        console.error('Error al cargar el catálogo de productos:', err);
      }
    });
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



}
