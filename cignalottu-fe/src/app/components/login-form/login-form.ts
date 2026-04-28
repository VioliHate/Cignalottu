import { Component, EventEmitter, inject, model, Output, output, signal } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ArrowRight, Eye, EyeOff, Lock, LucideAngularModule, Mail, User } from 'lucide-angular';
import { NgClass, NgOptimizedImage } from '@angular/common';
import { DialogRef } from '@angular/cdk/dialog';

@Component({
  selector: 'login-form',
  imports: [FormsModule, LucideAngularModule, ReactiveFormsModule, NgClass, NgOptimizedImage],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
})
export class LoginForm {
  private dialogRef = inject(DialogRef); // <--- Magia della DI
  protected readonly userIcon = User;
  protected readonly mailIcon = Mail;
  protected readonly lockIcon = Lock;
  protected readonly arrowRightIcon = ArrowRight;
  protected readonly eyeIcon = Eye;
  protected readonly eyeOffIcon = EyeOff;

  isLogin = model<boolean>(true);
  email = '';
  password = '';
  lastName = '';
  firstName = '';

  showPassword = signal(false);
  loading = signal(false);
  error = signal('');

  toggleLogin(value: boolean) {
    this.isLogin.set(value);
  }

  toggleShowPassword() {
    this.showPassword.update((value) => !value);
  }

  onSubmit() {
    this.loading.set(true);
    this.error.set('');

    setTimeout(() => {
      if (this.email === 'test@test.it') {
        const userData = { email: this.email, token: '12345' };

        if (this.dialogRef) {
          this.dialogRef.close(userData);
        }
      } else {
        this.error.set('Credenziali non valide');
        this.loading.set(false);
      }
    }, 1500);
  }

  loginWithGoogle() {
    console.log('Avvio login con Google');

    // this.authService.googleSignIn();
  }

  protected switchIsLogin() {
    this.isLogin.update((value) => !value);
  }
}
