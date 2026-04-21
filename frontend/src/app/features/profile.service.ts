import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  // Usamos una Signal (lo más moderno de Angular) para manejar el estado
  isOpen = signal<boolean>(false);

  toggle() {
    this.isOpen.update(value => !value);
  }

  open() {
    this.isOpen.set(true);
  }

  close() {
    this.isOpen.set(false);
  }
}