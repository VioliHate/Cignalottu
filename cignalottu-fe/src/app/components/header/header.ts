import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {NgOptimizedImage} from '@angular/common';
import {LucideAngularModule, ShoppingBag, User, UserIcon} from 'lucide-angular';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    NgOptimizedImage,
    LucideAngularModule
  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {

  mobileMenuOpen = false;
  cartCount = 2;
  categories =["Prodotti","Shop", "Per il Professionista", "Contatti", "Offerte"];

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  protected readonly ShoppingBag = ShoppingBag;
  protected readonly UserIcon = UserIcon;
}
