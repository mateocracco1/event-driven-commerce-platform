// admin/products/product.model.ts

export interface Category {
  id: number;
  name: string;
}
// src/app/core/models/product.model.ts

export interface Product {
  id?: number;
  code: string;
  name: string;
  description: string;
  price: number;
  stock: number;         // Ya va a mapear perfecto
  categoryName?: string; // Machea con el DTO
  imageUrl?: string;
  minStock?: number;
  lowStock?: boolean;    // Usamos el cálculo que viene de Spring
  categoryId?: number;   // Este lo seguimos usando solo para el formulario (el POST)
}