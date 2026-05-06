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

  readonly starRange = [1, 2, 3, 4, 5];

  getStarType(index: number): 'full' | 'half' | 'empty' {
    const rating = this.product().rating;

    if (rating >= index) {
      return 'full';
    }
    const decimalPart = rating % 1;
    const integralPart = Math.floor(rating);

    if (index === integralPart + 1) {
      if (decimalPart >= 0.8) return 'full';
      if (decimalPart >= 0.3) return 'half';
    }

    return 'empty';
  }
}
