import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Importar esto
@Component({
  selector: 'app-reset-password',
imports: [
    ReactiveFormsModule, 
    RouterLink,
    MatCardModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.scss',
})
export class ResetPasswordComponent {

  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private token = '';
  private router = inject(Router); // Para navegar
  private snackBar = inject(MatSnackBar); // Para el mensajito flotante
  hide = true;


 resetForm = this.fb.group({
    newPassword: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  }, {
    // Validación personalizada: comparamos los dos campos
    validators: (group: any) => {
      const pass = group.get('newPassword').value;
      const confirmPass = group.get('confirmPassword').value;
      return pass === confirmPass ? null : { notSame: true };
    }
  });

  constructor() {
    // Capturamos el token que viene en la URL (?token=...)
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
  }

  onReset() {
    if (this.resetForm.valid && this.token) {
      const payload = {
        token: this.token,
        newPassword: this.resetForm.value.newPassword
      };
      
      this.authService.resetPassword(payload).subscribe({
        next: () => {
          this.snackBar.open('¡Contraseña cambiada con éxito!', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.snackBar.open('El link expiró o es inválido.', 'Cerrar', { duration: 3000 });
        }
      });
    }

  }

}
