import { ProductCategory } from './ProductCategory';

export interface Product {
  id: number;
  name: string;
  category: ProductCategory;
  price: number;
  originalPrice?: number;
  image: string;
  description: string;
  inStock: boolean;
  rating: number;
  badge?: 'new' | 'best' | 'sale';
}
