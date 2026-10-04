import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/auth';

  // Guardamos el estado en memoria
  private authenticated = false;

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signin`, credentials, {
      withCredentials: true
    }).pipe(
      tap((res: any) => {
        localStorage.setItem('logged', 'true');
        localStorage.setItem('userName', res.name);
        // GUARDAMOS LOS ROLES (Suponiendo que el back manda un array ['ROLE_ADMIN'])
        localStorage.setItem('roles', JSON.stringify(res.roles));
      })
    );
  }

  hasRole(role: string): boolean {
    const rolesRaw = localStorage.getItem('roles');
    if (!rolesRaw) return false;

    const roles: string[] = JSON.parse(rolesRaw);
    return roles.includes(role);
  }


  isLoggedIn(): boolean {
    return localStorage.getItem('logged') === 'true';
  }

  logout() {
    localStorage.clear();
    window.location.href = '/'; // Recarga la app limpia
  }



  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, userData);
  }


  // ¿Olvidaste la pww?

  forgotPassword(email: string): Observable<string> {
    // Pasamos el email como un objeto que coincida con tu ForgotPasswordRequest DTO
    return this.http.post(`${this.apiUrl}/forgot-password`, { email }, { responseType: 'text' });
  }

  resetPassword(data: any): Observable<string> {
    // data debe contener { token, newPassword }
    return this.http.post(`${this.apiUrl}/reset-password`, data, { responseType: 'text' });
  }



}