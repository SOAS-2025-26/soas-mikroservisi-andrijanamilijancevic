import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-crypto-wallet',
  imports: [CommonModule, FormsModule],
  templateUrl: './crypto-wallet.html',
  styleUrl: './crypto-wallet.css'
})
export class CryptoWalletComponent implements OnInit {

  wallets: any[] = [];       // ADMIN: svi novcanici svih korisnika
  myWallets: any[] = [];     // USER: sve moje kripto valute

  errorMessage: string = '';
  successMessage: string = '';

  newWallet = { email: '', currencyCode: 'ETH', amount: 0 };
  updateData = { email: '', currencyCode: '', amount: 0 };
  deleteEmail: string = '';

  constructor(public api: ApiService, public auth: AuthService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.errorMessage = '';
    if (this.auth.isAdmin()) {
      this.api.getAllWallets().subscribe({
        next: (data: any) => this.wallets = data,
        error: () => this.errorMessage = 'Greška pri učitavanju novčanika!'
      });
    } else if (this.auth.isUser()) {
      this.api.getWalletsByEmail(this.auth.getEmail()).subscribe({
        next: (data: any) => this.myWallets = data,
        error: () => {
          this.myWallets = [];
          this.errorMessage = 'Nemate nijedan kripto novčanik!';
        }
      });
    }
  }

  createWallet() {
    this.api.createWallet(this.newWallet).subscribe({
      next: () => {
        this.successMessage = 'Novčanik uspešno kreiran!';
        this.loadData();
      },
      error: (err) => this.errorMessage = err.error?.message || err.error || 'Greška!'
    });
  }

  updateWallet() {
    this.api.updateWallet(this.updateData).subscribe({
      next: () => {
        this.successMessage = 'Novčanik uspešno ažuriran!';
        this.loadData();
      },
      error: (err) => this.errorMessage = err.error?.message || err.error || 'Greška!'
    });
  }

  deleteWallet() {
    this.api.deleteWallet(this.deleteEmail).subscribe({
      next: () => {
        this.successMessage = 'Novčanik uspešno obrisan!';
        this.loadData();
      },
      error: (err) => this.errorMessage = err.error?.message || err.error || 'Greška!'
    });
  }
}