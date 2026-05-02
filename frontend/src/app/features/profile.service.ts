import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  // Usamos una Signal (lo más moderno de Angular) para manejar el estado
  isOpen = signal<boolean>(false);
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/users';



  toggle() {
    this.isOpen.update(value => !value);
  }

  open() {
    this.isOpen.set(true);
  }

  close() {
    this.isOpen.set(false);
  }

  getMyProfile(): Observable<any> {
  
    return this.http.get(`${this.apiUrl}/me`, { withCredentials: true });
  }

  updateMyProfile(userData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/me`, userData, { withCredentials: true });
  }

// En profile.service.ts

/**
 * Envía la petición para actualizar la contraseña del usuario autenticado.
 * @param passwordData Objeto con { currentPassword, newPassword, confirmPassword }
 */
updatePassword(passwordData: any): Observable<any> {
  return this.http.patch(`${this.apiUrl}/me/password`, passwordData, { withCredentials: true });
}

}