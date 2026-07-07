import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  errorMessage: string = '';
  loading: boolean = false;

  constructor(private auth: AuthService, private api: ApiService, private router: Router) {}

  login() {
    if (!this.email || !this.password) {
      this.errorMessage = 'Unesite email i lozinku!';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.api.loginUser(this.email, this.password).subscribe({
      next: (data: any) => {
        const role = data.role;
        this.auth.login(this.email, this.password, role);
        this.loading = false;
        this.router.navigate(['/currency-exchange']);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 401) {
          this.errorMessage = 'Pogrešna lozinka!';
        } else if (err.status === 404) {
          this.errorMessage = 'Korisnik ne postoji!';
        } else {
          this.errorMessage = 'Greška pri logovanju!';
        }
      }
    });
  }
}