import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from "@angular/material/icon";
import { ResellerService } from './reseller.service';

@Component({
  selector: 'app-reseller-register',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatCardModule,
    MatFormFieldModule, MatIconModule],
  templateUrl: './reseller-register.html',
  styleUrl: './reseller-register.scss',
})
export class ResellerRegisterComponet {

  private fb = inject(FormBuilder);
  public hide = true;
  
  private resellerService = inject(ResellerService);
  public isSubmitted = false; // Para mostrar el mensaje de éxito
  public isLoading = false;
  private cdr = inject(ChangeDetectorRef);



  public resellerForm: FormGroup = this.fb.group({
    // Datos de Usuario
    name: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    
    // Datos de Revendedor
    isCompany: [false], // El check dinámico
    taxId: ['', Validators.required], // CUIT/CUIL
    businessName: [''] // Opcional al principio, dinámico después
  });

  onSubmit() {
    if (this.resellerForm.valid) {
      this.isLoading = true;
      this.resellerService.register(this.resellerForm.value).subscribe({
        next: (res) => {
          console.log('Respuesta exitosa del servidor:', res);
          this.isLoading = false;
          this.isSubmitted = true; // Cambiamos la vista
          this.cdr.detectChanges();
        },
        error: (err) => {
        this.isLoading = false;
        if (err.status === 401) {
          console.error("Error de permisos: Revisá la config de Spring Security.");
          this.cdr.detectChanges();
        } else if (err.status === 500 || err.status === 400) {
          alert("El email ya está registrado o hay un error en los datos.");
          this.cdr.detectChanges();
        }
      }
    });
  }
}

}
