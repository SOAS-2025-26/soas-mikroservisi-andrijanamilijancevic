import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-currency-exchange',
  imports: [CommonModule, FormsModule],
  templateUrl: './currency-exchange.html',
  styleUrl: './currency-exchange.css'
})
export class CurrencyExchangeComponent {

  from: string = 'USD';
  to: string = 'EUR';
  result: any = null;
  errorMessage: string = '';

  constructor(private api: ApiService) {}

  getRate() {
    this.errorMessage = '';
    this.result = null;
    this.api.getCurrencyExchange(this.from, this.to).subscribe({
      next: (data) => this.result = data,
      error: (err) => this.errorMessage = 'Greška pri dohvatanju kursa!'
    });
  }
}