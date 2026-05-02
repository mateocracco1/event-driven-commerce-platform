import { Component, OnInit, effect, inject } from '@angular/core'; 
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProfileService } from '../../profile.service';
import { MatIconModule } from "@angular/material/icon";

@Component({
  selector: 'app-profile-drawer',
  standalone: true, 
  imports: [CommonModule, ReactiveFormsModule, MatIconModule],
  templateUrl: './profile-drawer.html',
  styleUrl: './profile-drawer.scss',
})
export class ProfileDrawerComponent implements OnInit {
  
  private fb = inject(FormBuilder); 
  public profileService = inject(ProfileService);  // ventanita perfil
  
  profileForm!: FormGroup; 
  passwordForm!: FormGroup;   // para el reset 
  isEditing: boolean = false; 
  isChangingPassword: boolean = false;
  // para mostrar  las 3 contas...
  showCurrent: boolean = false;
  showNew: boolean = false; 
  showConfirm: boolean = false;




constructor() {
    // El effect se ejecuta cada vez que las Signals dentro de él cambian
    effect(() => {
      if (this.profileService.isOpen()) {
        this.loadUserData(); 
      }
    });
  }

  ngOnInit(): void {
    this.initForm(); 
    this.initPasswordForm();
  
  }
  
  initForm(): void {
    this.profileForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      lastName: ['', [Validators.required]],
      // Email deshabilitado: se ve pero no se edita ni se envía en el value
      email: [{ value: '', disabled: true }, [Validators.email]] 
    });
  }

  initPasswordForm(): void {
  this.passwordForm = this.fb.group({
    // Usamos el valor inicial '' y nos aseguramos de que no sea nulo
    currentPassword: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
    newPassword: this.fb.control('', { nonNullable: true, validators: [Validators.required, Validators.minLength(8)] }),
    confirmPassword: this.fb.control('', { nonNullable: true, validators: [Validators.required] })
    }, { 
    validators: [this.passwordMatchValidator] // Nota: los validadores de grupo van en un array en las versiones nuevas
    });
  } 
// Validador de coincidencia
  passwordMatchValidator(form: FormGroup) {
    const newPw = form.get('newPassword')?.value;
    const confirmPw = form.get('confirmPassword')?.value;
    return newPw === confirmPw ? null : { mismatch: true };
  }



  //traer datos de user de back
  loadUserData(): void {
    this.profileService.getMyProfile().subscribe({
      next: (user) => {
        // patchValue rellena los campos que coincidan con el objeto user (nombre, apellido, email)
        this.profileForm.patchValue(user);
      },
      error: (err) => console.error("Error al cargar perfil", err)
    });
  }



  savePassword() {
    if (this.passwordForm.valid) {
      this.profileService.updatePassword(this.passwordForm.value).subscribe({
        next: () => {
          console.log("Password cambiada");
          this.togglePasswordView(); // Volver al perfil
          this.passwordForm.reset();
        },
        error: (err) => alert("La contraseña actual es incorrecta")
      });
    }
  }

  togglePasswordView() {
    this.isChangingPassword = !this.isChangingPassword;
    // Si estamos saliendo de la vista de contraseña (volviendo al perfil), limpiamos
    if (!this.isChangingPassword) {
      this.resetPasswordState(); 
    }
  }

  // para las 3 contraseñas y vista de contraseñas
  togglePasswordVisibility(field: string) {
  if (field === 'current') this.showCurrent = !this.showCurrent;
  if (field === 'new') this.showNew = !this.showNew;
  if (field === 'confirm') this.showConfirm = !this.showConfirm;
  }

  saveProfile() {
    if (this.profileForm.valid) {
      // Usamos el nuevo método sin ID
      this.profileService.updateMyProfile(this.profileForm.value).subscribe({
        next: (res) => {
          console.log("Guardado con éxito");
          this.isEditing = false;
          // Opcional: actualizar el nombre en el localStorage si cambió
          localStorage.setItem('userName', res.name);
        },
        error: (err) => console.error("Error al guardar", err)
      });
    }
  }


  toggleEdit() {
    this.isEditing = !this.isEditing;
  }


 closeDrawer() {    
    this.profileService.close();
    this.isEditing = false; 
    this.isChangingPassword = false; 
    // Si el usuario cierra el panel con la "X" o haciendo clic afuera, también limpiamos
    this.resetPasswordState(); 
  }

  // Método auxiliar para limpiar todo el estado de seguridad 
  private resetPasswordState() {
    if (this.passwordForm) {
      this.passwordForm.reset(); // Limpia los inputs
    }
    // Volvemos a ocultar las contraseñas (reseteamos los ojitos)
    this.showCurrent = false;
    this.showNew = false;
    this.showConfirm = false;
  }
  


}