import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth';
import { MatCardHeader, MatCard, MatCardSubtitle, MatCardTitle, MatCardContent, MatCardModule } from "@angular/material/card";
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { MatFormField, MatLabel, MatError, MatFormFieldModule } from "@angular/material/form-field";
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-forgot-password',
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
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss',
})
export class ForgotPasswordComponent {

  mailEnviado = false;
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef); // <-- Inyectarlo acá
  

  forgotForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });


  onSend() {
    if (this.forgotForm.valid) {
      const email = this.forgotForm.value.email!;
      this.authService.forgotPassword(email).subscribe({
       next: (msg) => {
          // Si todo salió bien, cambiamos la bandera a true
          this.mailEnviado = true;
          this.cdr.detectChanges(); // para avisar a angular que hay cambios o algo
          console.log('Mail enviado:D')
        },
       error: (err) => {
          console.error('El error real es:', err);
          alert('Error al procesar la solicitud');
        }
      });
    }
  }
}
