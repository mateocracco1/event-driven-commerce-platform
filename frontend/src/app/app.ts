import { Component, signal, inject } from '@angular/core'; // Agregamos inject
import { RouterOutlet, Router } from '@angular/router'; // Agregamos Router
import { ProfileDrawerComponent } from "./features/profile/profile-drawer/profile-drawer";
import { NavbarComponent } from './shared/components/navbar/navbar';
import { CommonModule } from '@angular/common'; // Importante para usar @if

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, ProfileDrawerComponent, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend');
  
  // Inyectamos el router para usarlo en el HTML
  public router = inject(Router);
}