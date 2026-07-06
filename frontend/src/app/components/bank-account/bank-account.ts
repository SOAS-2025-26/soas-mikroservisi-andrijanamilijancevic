import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-bank-account',
  imports: [CommonModule, FormsModule],
  templateUrl: './bank-account.html',
  styleUrl: './bank-account.css'
})
export class BankAccountComponent implements OnInit {

  accounts: any[] = [];
  account: any = null;
  errorMessage: string = '';
  successMessage: string = '';

  newAccount = { email: '', currencyCode: 'EUR', amount: 0 };
  updateData = { email: '', currencyCode: '', amount: 0 };
  deleteEmail: string = '';

  constructor(public api: ApiService, public auth: AuthService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.errorMessage = '';
    if (this.auth.isAdmin()) {
      this.api.getAllAccounts().subscribe({
        next: (data: any) => this.accounts = data,
        error: () => this.errorMessage = 'Greška pri učitavanju računa!'
      });
    } else if (this.auth.isUser()) {
      this.api.getAccountByEmail(this.auth.getEmail()).subscribe({
        next: (data) => this.account = data,
        error: () => this.errorMessage = 'Račun nije pronađen!'
      });
    }
  }

  createAccount() {
    this.api.createAccount(this.newAccount).subscribe({
      next: () => {
        this.successMessage = 'Račun uspešno kreiran!';
        this.loadData();
      },
      error: (err) => this.errorMessage = err.error || 'Greška!'
    });
  }

  updateAccount() {
    this.api.updateAccount(this.updateData).subscribe({
      next: () => {
        this.successMessage = 'Račun uspešno ažuriran!';
        this.loadData();
      },
      error: (err) => this.errorMessage = err.error || 'Greška!'
    });
  }

  deleteAccount() {
    this.api.deleteAccount(this.deleteEmail).subscribe({
      next: () => {
        this.successMessage = 'Račun uspešno obrisan!';
        this.loadData();
      },
      error: (err) => this.errorMessage = err.error || 'Greška!'
    });
  }
}