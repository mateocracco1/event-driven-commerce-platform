import { Component, EventEmitter, Input, OnInit, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../AdminService/admin.service';
import { Product, Category } from '../../../../core/models/product.model';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-form.html',
  styleUrls: ['./product-form.scss']
})
export class ProductFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminService);

  // --- COMUNICACIÓN CON EL PADRE (product-list) ---
  @Input() productToEdit: Product | null = null; // Si viene un producto, estamos editando
  @Output() onClose = new EventEmitter<void>();   // Evento para cerrar el modal
  @Output() onSave = new EventEmitter<void>();    // Evento para avisar que se guardó con éxito

  productForm!: FormGroup;
  categories = signal<Category[]>([]); // Signal para cargar las categorías en el select

  ngOnInit(): void {
    this.initForm();
    this.loadCategoriesAndPatch();
  
    // Si estamos editando, deshabilitamos el campo stock para que usen el flujo de inventario
    if (this.productToEdit) {
      this.productForm.get('stock')?.disable();
    }
  }

  // Inicializamos el formulario con las validaciones requeridas
  private initForm(): void {
    this.productForm = this.fb.group({
      id: [null], 
      code: ['', [Validators.required, Validators.minLength(3)]],
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      price: [0, [Validators.required, Validators.min(0.01)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      minStock: [5, [Validators.required, Validators.min(0)]], // 
      categoryId: ['', [Validators.required]]
    });
  }

  // Cargamos categorías y manejamos el mapeo por si es edición
  private loadCategoriesAndPatch(): void {
    this.adminService.getAllCategories().subscribe({
      next: (data) => {
        this.categories.set(data);

        // Si venimos en modo edición, cargamos los datos del producto
        if (this.productToEdit) {
          this.productForm.patchValue(this.productToEdit);

          // TRUCO DE MAPEO: Como el DTO trae categoryName (String) y el select busca un categoryId (Long),
          // buscamos en la lista de categorías cuál coincide con el nombre para setear su ID.
          if (this.productToEdit.categoryName) {
            const foundCategory = data.find(c => c.name === this.productToEdit?.categoryName);
            if (foundCategory) {
              this.productForm.patchValue({ categoryId: foundCategory.id });
            }
          }
        }
      },
      error: (err) => console.error('Error al cargar categorías en el formulario:', err)
    });
  }

  // Se ejecuta al hacer el submit del formulario (Creación o Edición)
  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched(); 
      return;
    }

// En vez de: const productData: Product = this.productForm.value;
    const productData: Product = this.productForm.getRawValue(); // <-- Trae el stock deshabilitado!


   if (this.productToEdit && this.productToEdit.id) {
      
      // --- MODO MODIFICACIÓN (PUT) ---
      this.adminService.updateProduct(this.productToEdit.id, productData).subscribe({
        next: () => {
          alert('Producto modificado correctamente');
          this.onSave.emit();
        },
        error: (err) => {
          console.error('Error al modificar el producto:', err);
          alert('Hubo un error al modificar el producto');
        }
      });

    } else {
      // --- MODO CREACIÓN (POST) ---
      this.adminService.createProduct(productData).subscribe({
        next: () => {
          alert('Producto creado correctamente');
          this.onSave.emit();
        },
        error: (err) => {
          console.error('Error al guardar el producto:', err);
          alert('Hubo un error al guardar el producto');
        }
      });
    }
  }
  

  // Al presionar el botón Cancelar
  cancel(): void {
    this.onClose.emit(); 
  }
}