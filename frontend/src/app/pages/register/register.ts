import { Component, inject } from '@angular/core';
import { MatCard, MatCardHeader, MatCardTitle, MatCardSubtitle, MatCardContent, MatCardModule } from "@angular/material/card";
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { MatFormField, MatLabel, MatError, MatFormFieldModule } from "@angular/material/form-field";
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, 
    RouterLink,
    MatCardModule, 
    MatIconModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatButtonModule,
    MatProgressSpinnerModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class RegisterComponent {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  isLoading = false;
  hide = true;
 
  registerForm = this.fb.group({
    name: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });



  onRegister() {
    if (this.registerForm.valid) {
      this.isLoading = true; 
      
      this.authService.register(this.registerForm.value).subscribe({
        next: (response) => {
          this.isLoading = false; 
          alert('¡Registro exitoso!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.isLoading = false; // 
          alert('Error al registrarse');
        }
      });
    }
  }
}