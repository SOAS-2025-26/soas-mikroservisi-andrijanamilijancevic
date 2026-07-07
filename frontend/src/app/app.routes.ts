import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { CurrencyExchangeComponent } from './components/currency-exchange/currency-exchange';
import { CryptoExchangeComponent } from './components/crypto-exchange/crypto-exchange';
import { TradeComponent } from './components/trade/trade';
import { BankAccountComponent } from './components/bank-account/bank-account';
import { CurrencyConversionComponent } from './components/currency-conversion/currency-conversion';
import { CryptoWalletComponent } from './components/crypto-wallet/crypto-wallet';
import { UsersComponent } from './components/users/users';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'currency-exchange', component: CurrencyExchangeComponent },
  { path: 'crypto-exchange', component: CryptoExchangeComponent },
  { path: 'currency-conversion', component: CurrencyConversionComponent, canActivate: [authGuard] },
  { path: 'trade', component: TradeComponent, canActivate: [authGuard] },
  { path: 'bank-account', component: BankAccountComponent, canActivate: [authGuard] },
  { path: 'crypto-wallet', component: CryptoWalletComponent, canActivate: [authGuard] },
  { path: 'users', component: UsersComponent, canActivate: [authGuard] }
];