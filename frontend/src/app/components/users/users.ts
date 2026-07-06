import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-users',
  imports: [CommonModule, FormsModule],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class UsersComponent implements OnInit {

  users: any[] = [];
  errorMessage: string = '';
  successMessage: string = '';

  newUser = { email: '', password: '', role: 'USER' };
  updateData = { email: '', password: '', role: 'USER' };
  deleteEmail: string = '';

  constructor(public api: ApiService, public auth: AuthService) {}

  ngOnInit() {
     console.log('Credentials:', this.auth.getCredentials());
    console.log('Role:', this.auth.getRole());
    this.loadUsers();
  }

  loadUsers() {
    this.errorMessage = '';
    this.api.getAllUsers().subscribe({
      next: (data: any) => this.users = data,
      error: () => this.errorMessage = 'Greška pri učitavanju korisnika!'
    });
  }

  createUser() {
    this.api.createUser(this.newUser).subscribe({
      next: () => {
        this.successMessage = 'Korisnik uspešno kreiran!';
        this.loadUsers();
      },
      error: (err) => this.errorMessage = err.error || 'Greška!'
    });
  }

  updateUser() {
    this.api.updateUser(this.updateData).subscribe({
      next: () => {
        this.successMessage = 'Korisnik uspešno ažuriran!';
        this.loadUsers();
      },
      error: (err) => this.errorMessage = err.error || 'Greška!'
    });
  }

  deleteUser() {
    this.api.deleteUser(this.deleteEmail).subscribe({
      next: () => {
        this.successMessage = 'Korisnik uspešno obrisan!';
        this.loadUsers();
      },
      error: (err) => this.errorMessage = err.error || 'Greška!'
    });
  }
}