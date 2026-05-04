import { Component, inject, model, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ArrowRight, Eye, EyeOff, Lock, LucideAngularModule, Mail, User } from 'lucide-angular';
import { NgClass, NgOptimizedImage } from '@angular/common';
import { DialogRef } from '../../utils/dialog/dialog-ref';
@Component({
  selector: 'login-form',
  imports: [FormsModule, LucideAngularModule, ReactiveFormsModule, NgClass, NgOptimizedImage],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {
  private dialogRef = inject(DialogRef);
  protected readonly userIcon = User;
  protected readonly mailIcon = Mail;
  protected readonly lockIcon = Lock;
  protected readonly arrowRightIcon = ArrowRight;
  protected readonly eyeIcon = Eye;
  protected readonly eyeOffIcon = EyeOff;

  isLogin = signal(true);
  email = '';
  password = '';
  lastName = '';
  firstName = '';

  showPassword = signal(false);
  isLoading = signal(false);
  error = signal('');

  toggleLogin(value: boolean) {
    this.isLogin.set(value);
  }

  toggleShowPassword() {
    this.showPassword.update((value) => !value);
  }

  onSubmit() {
    console.log('INVIO CREDENZIALI ->', {
      email: this.email,
      password: this.password,
      lastname: this.lastName,
      fistname: this.firstName,
    });
    this.isLoading.set(true);
    this.error.set('');

    //gestire qui la chiamata al BE
  }

  loginWithGoogle() {
    console.log('Avvio login con Google');
  }

  protected switchIsLogin() {
    this.isLogin.update((value) => !value);
  }
}
