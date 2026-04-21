import { Component } from '@angular/core';
import { MatCard, MatCardTitle, MatCardHeader, MatCardContent, MatCardActions } from "@angular/material/card";

@Component({
  selector: 'app-home',
  imports: [MatCard, MatCardTitle, MatCardHeader, MatCardContent, MatCardActions],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class HomeComponent {}
