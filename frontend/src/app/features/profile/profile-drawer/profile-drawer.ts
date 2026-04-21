import { Component, OnInit, inject } from '@angular/core'; 
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ProfileService } from '../../profile.service';

@Component({
  selector: 'app-profile-drawer',
  standalone: true, 
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile-drawer.html',
  styleUrl: './profile-drawer.scss',
})
export class ProfileDrawerComponent implements OnInit {
  
  private fb = inject(FormBuilder); 
  public profileService = inject(ProfileService);  // ventanita perfil
  
  profileForm!: FormGroup; 
  isEditing: boolean = false; 

  ngOnInit(): void {
    this.initForm(); 
  }
  
  initForm(): void {
    this.profileForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      apellido: ['', [Validators.required]],
      // Email deshabilitado: se ve pero no se edita ni se envía en el value
      email: [{ value: 'usuario@ejemplo.com', disabled: true }, [Validators.email]] 
    });
  }

  toggleEdit() {
    this.isEditing = !this.isEditing;
  }

  saveProfile() {
    if (this.profileForm.valid) {
      // .getRawValue() incluye los campos disabled si los necesitás
      console.log("Datos a enviar a Java:", this.profileForm.value);
      this.isEditing = false;
    }
  }

  closeDrawer() {
    this.profileService.close();
    this.isEditing = false; // Resetear el modo edición al cerrar
  }
}