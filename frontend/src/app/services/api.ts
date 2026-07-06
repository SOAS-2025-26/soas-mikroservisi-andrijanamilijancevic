import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl = 'http://localhost:8765';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Basic ${this.auth.getCredentials()}`,
      'Content-Type': 'application/json'
    });
  }

  // Currency Exchange (javno)
  getCurrencyExchange(from: string, to: string) {
    return this.http.get(`${this.baseUrl}/currency-exchange?from=${from}&to=${to}`);
  }

  // Crypto Exchange (javno)
  getCryptoExchange(from: string, to: string) {
    return this.http.get(`${this.baseUrl}/crypto-exchange?from=${from}&to=${to}`);
  }

  // Currency Conversion
  getCurrencyConversion(from: string, to: string, quantity: number) {
    return this.http.get(
      `${this.baseUrl}/currency-conversion?from=${from}&to=${to}&quantity=${quantity}`,
      { headers: this.getHeaders() }
    );
  }

  // Trade Service
  trade(from: string, to: string, quantity: number, email: string) {
    return this.http.get(
      `${this.baseUrl}/trade-service?from=${from}&to=${to}&quantity=${quantity}&email=${email}`,
      { headers: this.getHeaders() }
    );
  }

  // Users
  getAllUsers() {
    return this.http.get(`${this.baseUrl}/users`, { headers: this.getHeaders() });
  }

  getUserByEmail(email: string) {
    return this.http.get(`${this.baseUrl}/users/email?email=${email}`, { headers: this.getHeaders() });
  }

  createUser(user: any) {
    return this.http.post(`${this.baseUrl}/users`, user, { headers: this.getHeaders() });
  }

  updateUser(user: any) {
    return this.http.put(`${this.baseUrl}/users`, user, { headers: this.getHeaders() });
  }

  deleteUser(email: string) {
    return this.http.delete(`${this.baseUrl}/users?email=${email}`, { headers: this.getHeaders() });
  }

  // Bank Account
  getAllAccounts() {
    return this.http.get(`${this.baseUrl}/bank-account`, { headers: this.getHeaders() });
  }

  getAccountByEmail(email: string) {
    return this.http.get(`${this.baseUrl}/bank-account/email?email=${email}`, { headers: this.getHeaders() });
  }

  createAccount(account: any) {
    return this.http.post(`${this.baseUrl}/bank-account`, account, { headers: this.getHeaders() });
  }

  updateAccount(account: any) {
    return this.http.put(`${this.baseUrl}/bank-account`, account, { headers: this.getHeaders() });
  }

  deleteAccount(email: string) {
    return this.http.delete(`${this.baseUrl}/bank-account?email=${email}`, { headers: this.getHeaders() });
  }

  // Crypto Wallet
  getAllWallets() {
    return this.http.get(`${this.baseUrl}/crypto-wallet`, { headers: this.getHeaders() });
  }

  getWalletByEmail(email: string) {
    return this.http.get(`${this.baseUrl}/crypto-wallet/email?email=${email}`, { headers: this.getHeaders() });
  }

  createWallet(wallet: any) {
    return this.http.post(`${this.baseUrl}/crypto-wallet`, wallet, { headers: this.getHeaders() });
  }

  updateWallet(wallet: any) {
    return this.http.put(`${this.baseUrl}/crypto-wallet`, wallet, { headers: this.getHeaders() });
  }

  deleteWallet(email: string) {
    return this.http.delete(`${this.baseUrl}/crypto-wallet?email=${email}`, { headers: this.getHeaders() });
  }
}