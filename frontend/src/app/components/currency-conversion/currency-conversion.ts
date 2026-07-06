import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';

@Component({
  selector: 'app-currency-conversion',
  imports: [CommonModule, FormsModule],
  templateUrl: './currency-conversion.html',
  styleUrl: './currency-conversion.css'
})
export class CurrencyConversionComponent {

  from: string = 'USD';
  to: string = 'EUR';
  quantity: number = 100;
  result: any = null;
  errorMessage: string = '';

  constructor(private api: ApiService) {}

  convert() {
    this.errorMessage = '';
    this.result = null;
    this.api.getCurrencyConversion(this.from, this.to, this.quantity).subscribe({
      next: (data) => this.result = data,
      error: (err) => this.errorMessage = 'Greška pri konverziji! Proverite da li ste ulogovani.'
    });
  }
}