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
    this.loadCategories();

    // Si viene un producto para editar, cargamos sus datos en el formulario
    if (this.productToEdit) {
      this.productForm.patchValue(this.productToEdit);
    }
  }

  // Inicializamos el formulario con las validaciones requeridas por tu User Story
  private initForm(): void {
    this.productForm = this.fb.group({
      id: [null], // Lo necesitamos oculto por si estamos editando
      code: ['', [Validators.required, Validators.minLength(3)]],
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      price: [0, [Validators.required, Validators.min(0.01)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      categoryId: ['', [Validators.required]]
    });
  }

  // Cargamos las categorías desde el backend para el <select>
  private loadCategories(): void {
    this.adminService.getAllCategories().subscribe({
      next: (data) => this.categories.set(data),
      error: (err) => console.error('Error al cargar categorías en el formulario:', err)
    });
  }

  // Se ejecuta al hacer el submit del formulario
  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched(); // Marca los errores visualmente si intentan mandar vacío
      return;
    }

    const productData: Product = this.productForm.value;

    // TODO: En un futuro paso podemos agregar acá la lógica de si edita (PUT) o crea (POST)
    // De momento, implementamos la creación directo de tu User Story
    this.adminService.createProduct(productData).subscribe({
      next: () => {
        alert('Producto guardado correctamente');
        this.onSave.emit(); // Le avisamos al padre que refresque la lista y cierre
      },
      error: (err) => {
        console.error('Error al guardar el producto:', err);
        alert('Hubo un error al guardar el producto');
      }
    });
  }

  // Al presionar el botón Cancelar
  cancel(): void {
    this.onClose.emit(); // Le avisa al padre que simplemente cierre el modal
  }
}