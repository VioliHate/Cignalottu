import { Routes } from '@angular/router';
import { ShopWindow } from './pages/shop-window/shop-window';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/shop',
    pathMatch: 'full',
  },
  {
    path: 'shop',
    component: ShopWindow,
  },
];
