import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterModule } from '@angular/router';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatBadgeModule } from '@angular/material/badge';
import { ProfileService } from '../../../features/profile/profile-drawer/profile.service';
import { AuthService } from '../../../services/auth';
import { CartService } from '../../../services/cart.service';


@Component({
  selector: 'app-navbar',
  imports: [
    RouterModule,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    MatBadgeModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class NavbarComponent {

  private authService = inject(AuthService);
  private router = inject(Router);
  public profileService = inject(ProfileService);
  public cartService = inject(CartService);

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get userName(): string {
    return localStorage.getItem('userName') || 'Usuario';
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  goToCart(): void {
    this.cartService.open();
  }
}
