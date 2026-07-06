import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private credentials: string = '';
  private userRole: string = '';
  private userEmail: string = '';

  login(email: string, password: string, role: string) {
    this.credentials = btoa(`${email}:${password}`);
    this.userRole = role;
    this.userEmail = email;
    localStorage.setItem('credentials', this.credentials);
    localStorage.setItem('role', role);
    localStorage.setItem('email', email);
  }

  logout() {
    this.credentials = '';
    this.userRole = '';
    this.userEmail = '';
    localStorage.removeItem('credentials');
    localStorage.removeItem('role');
    localStorage.removeItem('email');
  }

  getCredentials(): string {
    return localStorage.getItem('credentials') || '';
  }

  getRole(): string {
    return localStorage.getItem('role') || '';
  }

  getEmail(): string {
    return localStorage.getItem('email') || '';
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('credentials');
  }

  isUser(): boolean {
    return this.getRole() === 'USER';
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  isOwner(): boolean {
    return this.getRole() === 'OWNER';
  }
}