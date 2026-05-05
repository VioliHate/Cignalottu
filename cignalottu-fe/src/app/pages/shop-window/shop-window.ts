import { CommonModule } from '@angular/common';
import { Component, computed, signal } from '@angular/core';
import { ProductCard } from '../../components/product-card/product-card';
import { ProductCategory } from '../../model/ProductCategory';
import { Product } from '../../model/Product';
import { MOCK_PRODUCTS } from '../../mock/MOCK_PRODUCTS';
import { FilterBar } from '../../components/filter-bar/filter-bar';

@Component({
  selector: 'app-shop-window',
  standalone: true,
  imports: [CommonModule, ProductCard, FilterBar],
  templateUrl: './shop-window.html',
  styleUrl: './shop-window.css',
})
export class ShopWindow {
  readonly allCategories = Object.values(ProductCategory);
  selectedCategory = signal<ProductCategory>(ProductCategory.ALL);
  products = signal<Product[]>(MOCK_PRODUCTS);

  setFilter(cat: ProductCategory) {
    this.selectedCategory.set(cat);
  }

  filteredProducts = computed(() => {
    const activeCat = this.selectedCategory();
    const allProducts = this.products();
    if (activeCat === ProductCategory.ALL) {
      return allProducts;
    }
    return allProducts.filter((p) => p.category === activeCat);
  });

  updateFilter(newCategory: ProductCategory) {
    this.selectedCategory.set(newCategory);
  }
}
