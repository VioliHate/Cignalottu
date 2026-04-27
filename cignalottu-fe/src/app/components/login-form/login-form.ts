import { Component, EventEmitter, model, Output, output, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ArrowRight, Eye, EyeOff, Lock, LucideAngularModule, Mail, User } from 'lucide-angular';
import { NgClass, NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'login-form',
  imports: [FormsModule, LucideAngularModule, ReactiveFormsModule, NgClass, NgOptimizedImage],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {
  protected readonly userIcon = User;
  protected readonly mailIcon = Mail;
  protected readonly lockIcon = Lock;
  protected readonly arrowRightIcon = ArrowRight;
  protected readonly eyeIcon = Eye;
  protected readonly eyeOffIcon = EyeOff;

  isLogin = model<boolean>(true);
  email = model<string>('');
  password = model<string>('');
  lastName = model<string>('');
  firstName = model<string>('');

  @Output() formSubmit = new EventEmitter<any>();

  showPassword = signal(false);

  toggleLogin(value: boolean) {
    this.isLogin.set(value);
  }

  toggleShowPassword() {
    this.showPassword.update((value) => !value);
  }

  onSubmit() {
    console.log('1. Click sul tasto invio nel componente FIGLIO');
    this.formSubmit.emit({
      firstName: this.firstName(),
      lastName: this.lastName(),
      email: this.email(),
      password: this.password(),
      isLogin: this.isLogin(),
    });
  }

  loginWithGoogle() {
    console.log('Avvio login con Google');

    // this.authService.googleSignIn();
  }

  protected switchIsLogin() {
    this.isLogin.update((value) => !value);
  }
}
