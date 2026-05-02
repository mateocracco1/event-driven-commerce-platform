import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../../services/auth';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterModule, MatIconModule],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.component.scss' // No olvides darle estilo al sidebar!
})
export class AdminLayoutComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}