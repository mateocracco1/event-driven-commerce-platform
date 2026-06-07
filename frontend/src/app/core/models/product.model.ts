// admin/products/product.model.ts

export interface Category {
  id: number;
  name: string;
}

export interface Product {
  id?: number;         // Opcional porque al crear uno nuevo el backend genera el ID
  code: string;
  name: string;
  description: string;
  price: number;
  stock: number;
  categoryId: number;  // Usamos el ID de la categoría para enviarlo fácilmente en el formulari
}