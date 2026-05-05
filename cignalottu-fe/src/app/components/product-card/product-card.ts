import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { Product } from '../../model/Product';

@Component({
  selector: 'app-product-card',
  imports: [CommonModule],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCard {
  product = input.required<Product>();

  get stars() {
    return Array(5)
      .fill(0)
      .map((_, i) => i < this.product().rating);
  }
}
