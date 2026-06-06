import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ResellerService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/resellers'; 

 register(data: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/register`, data, { 
    withCredentials: true,
    responseType: 'text' as 'json' 
  });
}
}