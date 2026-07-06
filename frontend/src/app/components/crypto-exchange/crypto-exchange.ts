import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-crypto-exchange',
  imports: [CommonModule, FormsModule],
  templateUrl: './crypto-exchange.html',
  styleUrl: './crypto-exchange.css'
})
export class CryptoExchangeComponent {

  from: string = 'BTC';
  to: string = 'USD';
  result: any = null;
  errorMessage: string = '';

  constructor(private api: ApiService) {}

  getRate() {
    this.errorMessage = '';
    this.result = null;
    this.api.getCryptoExchange(this.from, this.to).subscribe({
      next: (data) => this.result = data,
      error: (err) => this.errorMessage = 'Greška pri dohvatanju kursa!'
    });
  }
}