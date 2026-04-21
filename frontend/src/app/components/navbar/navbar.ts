import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterModule } from '@angular/router';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../../services/auth';
import { MatDividerModule } from '@angular/material/divider';
import { ProfileService } from '../../features/profile.service';


@Component({
  selector: 'app-navbar',
  imports: [
    RouterModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class NavbarComponent {


  private authService = inject(AuthService);
  private router = inject(Router);
  public profileService = inject(ProfileService);

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }


  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }


  get userName(): string {
  return localStorage.getItem('userName') || 'Usuario';
}

}
