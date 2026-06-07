import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { AdminService } from '../../AdminService/admin.service';
import { Category } from '../../../../core/models/product.model';
import { CategoryFormComponent } from '../category-form/category-form';

@Component({
  selector: 'app-category-list',
  standalone: true, 
  imports: [CommonModule, MatIconModule, CategoryFormComponent],
  templateUrl: './category-list.html',
  styleUrl: './category-list.scss',
})
export class CategoryList implements OnInit {
  private adminService = inject(AdminService);

  categories = signal<Category[]>([]);
  showForm = signal<boolean>(false);
  selectedCategory = signal<Category | null>(null);

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.adminService.getAllCategories().subscribe({
      next: (data) => {
        this.categories.set(data);
      },
      error: (err) => {
        console.error('Error al cargar categorías:', err);
      }
    });
  }

  openCreateForm(): void {
    this.selectedCategory.set(null);
    this.showForm.set(true);
  }

  openEditForm(category: Category): void {
    this.selectedCategory.set(category);
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.selectedCategory.set(null);
  }

  onCategorySaved(): void {
    this.closeForm();
    this.loadCategories();
  }

  deleteCategory(id: number): void {
    if (confirm('¿Estás seguro de que querés eliminar esta categoría?')) {
      this.adminService.deleteCategory(id).subscribe({
        next: () => {
          this.categories.update(prev => prev.filter(c => c.id !== id));
        },
        error: (err) => {
          console.error('Error al eliminar la categoría:', err);
        }
      });
    }
  }
}
