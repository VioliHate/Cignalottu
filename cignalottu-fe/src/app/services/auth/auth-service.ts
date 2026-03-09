import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {computed, inject, Injectable, signal} from '@angular/core';
import {environment} from '../../../environments/environment';
import {AuthResponse} from '../../models/auth/auth-response';
import {LoginRequest} from '../../models/auth/login-request';
import {catchError, Observable, tap, throwError} from 'rxjs';
import {RegisterRequest} from '../../models/auth/register-request';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private http = inject(HttpClient);
  private router = inject(Router);

  private readonly apiUrl = `${environment.url}/api/auth`;

  // state signals
  private _user    = signal<AuthResponse['user'] | null>(null);
  private _token   = signal<string | null>(null);

  // public API
  readonly user           = this._user.asReadonly();
  readonly isAuthenticated = computed(() => !!this._token());
  readonly hasToken       = this.isAuthenticated;


  constructor() {
    this.#restoreSession();
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, payload).pipe(
      tap(res => this.#setSession(res)),
      catchError(err => this.#handleError(err))
    );
  }


  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap(res => this.#setSession(res)),
      catchError(err => this.#handleError(err))
    );
  }

  logout(reason: string = 'Logout manuale'): void {
    this._token.set(null);
    this._user.set(null);
    this.#clearStorage();
    console.info(`[Auth] Logout: ${reason}`);
    this.router.navigate(['/auth/login'], { replaceUrl: true });
  }

  getAccessToken(): string | null {
    return this._token();
  }



//helper - private
  #setSession(res: AuthResponse): void {
    this._token.set(res.accessToken);
    this._user.set(res.user ?? null);
    localStorage.setItem('access_token', res.accessToken);
    if (res.refreshToken) {
      localStorage.setItem('refresh_token', res.refreshToken);
    }
    if (res.user) {
      localStorage.setItem('user', JSON.stringify(res.user));
    }
  }

  #restoreSession(): void {
    const token = localStorage.getItem('access_token');
    const userJson = localStorage.getItem('user');
    if (!token) return;
    this._token.set(token);
    if (userJson) {
      try {
        this._user.set(JSON.parse(userJson));
      } catch {
        this.#clearStorage();
        this.logout('Invalid stored user data');
      }
    }
  }

  #clearStorage(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user');
  }

  #handleError(err: HttpErrorResponse)  {
    let msg = 'Errore sconosciuto';

    if (err.status === 0)               msg = 'Impossibile contattare il server';
    else if (err.status === 400)        msg = 'Dati non validi';
    else if (err.status === 401)        msg = 'Credenziali errate';
    else if (err.status === 409)        msg = 'Email già registrata';
    else if (err.status >= 500)         msg = 'Errore server';

    return throwError(() => new Error(msg));
  }
}
