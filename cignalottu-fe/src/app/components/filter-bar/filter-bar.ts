import { CommonModule } from '@angular/common';
import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-filter-bar',
  imports: [CommonModule],
  templateUrl: './filter-bar.html',
  styleUrl: './filter-bar.css',
})
export class FilterBar {
  categories = input.required<string[]>();
  activeCategory = input.required<string>();

  categoryChanged = output<string>();

  selectCategory(cat: string) {
    this.categoryChanged.emit(cat);
  }
}
