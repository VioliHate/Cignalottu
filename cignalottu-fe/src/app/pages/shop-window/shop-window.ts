import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { ProductCard } from '../../components/product-card/product-card';
import { ProductCategory } from '../../model/ProductCategory';
import { Product } from '../../model/Product';
import { MOCK_PRODUCTS } from '../../mock/MOCK_PRODUCTS';

@Component({
  selector: 'app-shop-window',
  standalone: true,
  imports: [CommonModule, ProductCard],
  templateUrl: './shop-window.html',
  styleUrl: './shop-window.css',
})
export class ShopWindow {
  readonly categories = Object.values(ProductCategory);

  selectedCategory = signal<ProductCategory>(ProductCategory.ALL);

  products = signal<Product[]>(MOCK_PRODUCTS);

  setFilter(cat: ProductCategory) {
    this.selectedCategory.set(cat);
  }

  filteredProducts = computed(() => {
    const active = this.selectedCategory();
    if (active === ProductCategory.ALL) return this.products();
    return this.products().filter((p) => p.category === active);
  });
}
