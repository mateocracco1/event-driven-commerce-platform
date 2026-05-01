import { Component, OnInit, effect, inject } from '@angular/core'; 
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
    
  }
  
  initForm(): void {
    this.profileForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      lastName: ['', [Validators.required]],
      // Email deshabilitado: se ve pero no se edita ni se envía en el value
      email: [{ value: '', disabled: true }, [Validators.email]] 
    });
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


  closeDrawer() {    // Para abrir perfil 
    this.profileService.close();
    this.isEditing = false; // Resetear el modo edición al cerrar
  }

  


}