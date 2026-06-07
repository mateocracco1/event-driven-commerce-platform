import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../AdminService/admin.service';
import { Category } from '../../../../core/models/product.model';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './category-form.html',
  styleUrls: ['./category-form.scss']
})
export class CategoryFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminService);

  @Input() categoryToEdit: Category | null = null;
  @Output() onClose = new EventEmitter<void>();
  @Output() onSave = new EventEmitter<void>();

  categoryForm!: FormGroup;

  ngOnInit(): void {
    this.initForm();

    if (this.categoryToEdit) {
      this.categoryForm.patchValue(this.categoryToEdit);
    }
  }

  private initForm(): void {
    this.categoryForm = this.fb.group({
      id: [null],
      name: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) {
      this.categoryForm.markAllAsTouched();
      return;
    }

    const categoryData: Category = this.categoryForm.value;

    if (this.categoryToEdit && this.categoryToEdit.id) {
      // --- MODO EDICIÓN (PUT) ---
      this.adminService.updateCategory(this.categoryToEdit.id, categoryData).subscribe({
        next: () => {
          alert('Categoría modificada correctamente');
          this.onSave.emit();
        },
        error: (err) => {
          console.error('Error al modificar la categoría:', err);
          alert('Hubo un error al modificar la categoría');
        }
      });
    } else {
      // --- MODO CREACIÓN (POST) ---
      this.adminService.createCategory(categoryData).subscribe({
        next: () => {
          alert('Categoría creada correctamente');
          this.onSave.emit();
        },
        error: (err) => {
          console.error('Error al guardar la categoría:', err);
          alert('Hubo un error al guardar la categoría');
        }
      });
    }
  }

  cancel(): void {
    this.onClose.emit();
  }
}