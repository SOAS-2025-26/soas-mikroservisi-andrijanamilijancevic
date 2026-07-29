import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-trade',
  imports: [CommonModule, FormsModule],
  templateUrl: './trade.html',
  styleUrl: './trade.css'
})
export class TradeComponent {

  from: string = 'ETH';
  to: string = 'USD';
  quantity: number = 1;
  result: any = null;
  errorMessage: string = '';

  constructor(private api: ApiService, private auth: AuthService) {}

 trade() {
    this.errorMessage = '';
    this.result = null;
    this.api.trade(this.from, this.to, this.quantity).subscribe({
      next: (data) => this.result = data,
      error: (err) => this.errorMessage = 'Greška pri razmeni! ' + (err.error?.message || '')
    });
  }
}