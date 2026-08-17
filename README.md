# SOAS Exchange

Aplikacija za razmenu običnih (fiat) i kripto valuta.

## Tehnologije
- Java 17, Spring Boot, Maven
- Angular frontend
- H2 in-memory baza podataka
- Docker, Eureka, Feign

---

## Kredencijali korisnika

| Email | Lozinka | Uloga |
|-------|---------|-------|
| owner@soas.com | owner123 | OWNER |
| admin@soas.com | admin123 | ADMIN |
| user@soas.com | user123 | USER |

---

## Funkcionalni URL-ovi (preko API Gateway-a na portu 8765)

### Javni endpointi (bez autentikacije)
| Opis | URL |
|------|-----|
| Kurs fiat valuta | `GET http://localhost:8765/currency-exchange?from=EUR&to=RSD` |
| Kurs kripto valuta | `GET http://localhost:8765/crypto-exchange?from=BTC&to=USD` |
| Login | `GET http://localhost:8765/users/login?email=owner@soas.com&password=owner123` |

### Users Service (OWNER i ADMIN)
| Opis | URL |
|------|-----|
| Lista svih korisnika | `GET http://localhost:8765/users` |
| Korisnik po emailu | `GET http://localhost:8765/users/email?email=user@soas.com` |
| Kreiraj korisnika | `POST http://localhost:8765/users` |
| Ažuriraj korisnika | `PUT http://localhost:8765/users` |
| Obriši korisnika | `DELETE http://localhost:8765/users?email=user@soas.com` |

### Bank Account (ADMIN i USER)
| Opis | URL |
|------|-----|
| Svi računi | `GET http://localhost:8765/bank-account` |
| Računi po emailu | `GET http://localhost:8765/bank-account/email?email=user@soas.com` |
| Račun po emailu i valuti | `GET http://localhost:8765/bank-account/email-currency?email=user@soas.com&currencyCode=EUR` |
| Kreiraj račun | `POST http://localhost:8765/bank-account` |
| Ažuriraj račun | `PUT http://localhost:8765/bank-account` |
| Obriši račune korisnika | `DELETE http://localhost:8765/bank-account?email=user@soas.com` |

### Crypto Wallet (ADMIN i USER)
| Opis | URL |
|------|-----|
| Svi novčanici | `GET http://localhost:8765/crypto-wallet` |
| Novčanici po emailu | `GET http://localhost:8765/crypto-wallet/email?email=user@soas.com` |
| Novčanik po emailu i valuti | `GET http://localhost:8765/crypto-wallet/email-currency?email=user@soas.com&currencyCode=ETH` |
| Kreiraj novčanik | `POST http://localhost:8765/crypto-wallet` |
| Ažuriraj novčanik | `PUT http://localhost:8765/crypto-wallet` |
| Obriši novčanike korisnika | `DELETE http://localhost:8765/crypto-wallet?email=user@soas.com` |

### Currency Conversion (samo USER)
| Opis | URL |
|------|-----|
| Razmena fiat valuta | `GET http://localhost:8765/currency-conversion?from=EUR&to=RSD&quantity=100` |

### Trade Service (samo USER)
| Opis | URL |
|------|-----|
| Razmena valuta | `GET http://localhost:8765/trade-service?from=EUR&to=ETH&quantity=100` |
| Crypto u fiat | `GET http://localhost:8765/trade-service?from=ETH&to=EUR&quantity=0.1` |
| Crypto u crypto | `GET http://localhost:8765/trade-service?from=ETH&to=BTC&quantity=0.1` |

---

## Docker

Slike su dostupne na Docker Hub-u: [andrijanamilijancevic](https://hub.docker.com/u/andrijanamilijancevic)

### Pokretanje aplikacije
```bash
docker compose up
```

### Mikroservisi i portovi
| Mikroservis | Port | Docker Hub |
|-------------|------|------------|
| NamingServer | 8761 | andrijanamilijancevic/naming-server:latest |
| ApiGateway | 8765 | andrijanamilijancevic/api-gateway:latest |
| UsersService | 8770 | andrijanamilijancevic/users-service:latest |
| BankAccount | 8200 | andrijanamilijancevic/bank-account:latest |
| CryptoWallet | 8300 | andrijanamilijancevic/crypto-wallet:latest |
| CurrencyExchange | 8000 | andrijanamilijancevic/currency-exchange:latest |
| CurrencyConversion | 8100 | andrijanamilijancevic/currency-conversion:latest |
| CryptoExchange | 8400 | andrijanamilijancevic/crypto-exchange:latest |
| TradeService | 8600 | andrijanamilijancevic/trade-service:latest |