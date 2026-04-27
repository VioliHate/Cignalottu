import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { LoginForm } from '../../components/login-form/login-form';

@Component({
  selector: 'app-login-page',
  imports: [CommonModule, FormsModule, LucideAngularModule, LoginForm],
  templateUrl: './login-page.html',
  styleUrl: './login-page.css',
})
export class LoginPage {
  isLogin = signal(true);
  email = signal('');
  password = signal('');

  onSubmit(data: any) {
    console.log('2. Dati ricevuti nel componente PADRE', data);

    this.email.set('');
    this.password.set('');
  }
}
