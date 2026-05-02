import { ChangeDetectorRef, Component, inject, OnInit, signal } from '@angular/core';
import { AdminService } from '../../AdminService/admin.service';
import { MatIconModule } from "@angular/material/icon";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-list',
  imports: [MatIconModule,CommonModule],
  templateUrl: './user-list.html',
  styleUrl:'./user-list.component.scss',
})
export class UserListComponent implements OnInit {

  
  private adminService = inject(AdminService);
  users = signal<any[]>([]);
  private cdr = inject(ChangeDetectorRef);  // avisar de cambios
  
  
  ngOnInit(): void {
    this.loadUsers();
  }

  
loadUsers() {
  this.adminService.getAllUsers().subscribe(data => this.users.set(data));
}

deleteUser(id: number) {
  this.adminService.deleteUser(id).subscribe(() => {
    // Actualizamos el signal filtrando el valor viejo
    this.users.update(prev => prev.filter(u => u.id != id));  // usamos el prev para trear lo mismo de delete para evitar ir al back
  });
}

  
editUser(_t14: any) {
throw new Error('Method not implemented.');
}


}

