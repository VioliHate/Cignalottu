import {Component, input, output, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ArrowRight, Eye, EyeOff, Lock, LucideAngularModule, Mail, Scissors, ShoppingBag, User} from "lucide-angular";
import {NgClass, NgIf} from "@angular/common";
import {email} from '@angular/forms/signals';

@Component({
  selector: 'login-form',
  imports: [
    FormsModule,
    LucideAngularModule,
    ReactiveFormsModule,
    NgClass
  ],
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

  isLogin = input<boolean>(true);
  email = input<string>('');
  password = input<string>('');

  isLoginChange = output<boolean>();
  emailChange = output<string>();
  passwordChange = output<string>();
  submit = output<{ email: string; password: string; isLogin: boolean }>();

  showPassword = signal(false);

  toggleLogin(value: boolean) {
    this.isLoginChange.emit(value);
  }

  toggleShowPassword() {
    this.showPassword.update(v => !v);
  }

  onSubmit() {
    this.submit.emit({
      email: this.email(),
      password: this.password(),
      isLogin: this.isLogin(),
    });
  }

  loginWithGoogle() {
    console.log('Avvio login con Google');

    // this.authService.googleSignIn();
  }
}
