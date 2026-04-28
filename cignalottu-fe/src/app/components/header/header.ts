import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';
import { LucideAngularModule, ShoppingBag, UserIcon } from 'lucide-angular';
import { LoginForm } from '../login-form/login-form';
import { DialogService } from '../../services/dialog/dialog-service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, NgOptimizedImage, LucideAngularModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  mobileMenuOpen = false;
  cartCount = 2;
  categories = ['Prodotti', 'Shop', 'Per il Professionista', 'Contatti', 'Offerte'];

  dialog = inject(DialogService);

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  protected readonly ShoppingBag = ShoppingBag;
  protected readonly UserIcon = UserIcon;

  protected openUserDialog() {
    const dialogRef = this.dialog.open({
      component: LoginForm,
      title: 'Accedi o Registrati',
      subTitle: 'Inserisci le tue credenziali per accedere',
    });
    console.log('0. Istanza DialogRef creata nel Header:', dialogRef);
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log('6. DATI ARRIVATI NEL HEADER:', result);
        console.log('Dati ricevuti nel componente Header:', result);
      }
    });
  }
}
