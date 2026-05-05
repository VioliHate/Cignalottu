import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { ProductCategory } from '../../model/ProductCategory';

@Component({
  selector: 'app-filter-bar',
  imports: [CommonModule],
  templateUrl: './filter-bar.html',
  styleUrl: './filter-bar.css',
})
export class FilterBar {
  categories = input.required<ProductCategory[]>();
  activeCategory = input.required<ProductCategory>();

  categoryChanged = output<string>();
  changed = output<ProductCategory>();

  select(cat: ProductCategory) {
    this.changed.emit(cat);
  }
}
