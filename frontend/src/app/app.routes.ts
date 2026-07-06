import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { CurrencyExchangeComponent } from './components/currency-exchange/currency-exchange';
import { CryptoExchangeComponent } from './components/crypto-exchange/crypto-exchange';
import { TradeComponent } from './components/trade/trade';
import { BankAccountComponent } from './components/bank-account/bank-account';
import { CurrencyConversionComponent } from './components/currency-conversion/currency-conversion';
import { CryptoWalletComponent } from './components/crypto-wallet/crypto-wallet';
import { UsersComponent } from './components/users/users';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'currency-exchange', component: CurrencyExchangeComponent },
  { path: 'crypto-exchange', component: CryptoExchangeComponent },
  { path: 'currency-conversion', component: CurrencyConversionComponent },
  { path: 'trade', component: TradeComponent },
  { path: 'bank-account', component: BankAccountComponent },
  { path: 'crypto-wallet', component: CryptoWalletComponent },
  { path: 'users', component: UsersComponent }
];