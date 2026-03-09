import {Component, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ArrowRight, LucideAngularModule, Mail, Scissors, ShoppingBag, User, Lock} from 'lucide-angular';
import {LoginForm} from '../../components/login-form/login-form';


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

  onSubmit(data: { email: string; password: string; isLogin: boolean }) {
    console.log('Form inviato:', {
      tipo: data.isLogin ? 'Login' : 'Registrazione',
      email: data.email,
      password: data.password,
    });

    this.email.set('');
    this.password.set('');
  }
}
