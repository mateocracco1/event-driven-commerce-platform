import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';
import { ProfileDrawerComponent } from "./features/profile/profile-drawer/profile-drawer";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, ProfileDrawerComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend');
}
