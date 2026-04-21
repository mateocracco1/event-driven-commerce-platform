import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    
    ReactiveFormsModule, 
    MatCardModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatButtonModule,
    MatIconModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class LoginComponent {

  private fb = inject(FormBuilder);

  private authService = inject(AuthService); 
  private router = inject(Router);

  
  loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });


 onLogin() {
    if (this.loginForm.valid) {
      // 1. Llamamos al método del servicio
      // 2. Nos SUSCRIBIMOS (aquí se dispara la petición)
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          console.log('¡Respuesta del Back!', response);
          // Si el login es correcto, navegamos al home
          this.router.navigate(['/home']);
        },
        error: (err) => {
          console.error('Error al iniciar sesión:', err);
          // Aquí podrías poner un mensaje de "Usuario o clave incorrectos"
          alert('Error: Credenciales no válidas o problema de conexión');
        }
      });
    }
  }
}