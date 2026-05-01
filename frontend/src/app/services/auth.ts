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
      tap((res : any) => {
    localStorage.setItem('logged', 'true');
    localStorage.setItem('userName', res.name); // Guardamos el nombre que viene del Java
  })
      
    );
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('logged') === 'true';
  }

  logout() {
    localStorage.removeItem('logged');
    localStorage.removeItem('userName');
  }



  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, userData); 
  }


  


}