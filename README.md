# SOAS Project - Currency & Crypto Exchange Application

## Mikroservisi i URL-ovi

### Preko API Gateway-a (port 8765)

| Servis | URL | Metoda |
|--------|-----|--------|
| Currency Exchange | http://localhost:8765/currency-exchange?from=USD&to=EUR | GET |
| Currency Conversion | http://localhost:8765/currency-conversion?from=USD&to=EUR&quantity=100 | GET |
| Trade Service | http://localhost:8765/trade-service?from=ETH&to=USD&quantity=1&email=user@soas.com | GET |
| Users - svi | http://localhost:8765/users | GET |
| Users - po emailu | http://localhost:8765/users/email?email=user@soas.com | GET |
| Users - dodaj | http://localhost:8765/users | POST |
| Users - azuriraj | http://localhost:8765/users | PUT |
| Users - obrisi | http://localhost:8765/users?email=user@soas.com | DELETE |
| Bank Account - svi | http://localhost:8765/bank-account | GET |
| Bank Account - po emailu | http://localhost:8765/bank-account/email?email=user@soas.com | GET |
| Bank Account - dodaj | http://localhost:8765/bank-account | POST |
| Bank Account - azuriraj | http://localhost:8765/bank-account | PUT |
| Bank Account - obrisi | http://localhost:8765/bank-account?email=user@soas.com | DELETE |
| Crypto Wallet - svi | http://localhost:8765/crypto-wallet | GET |
| Crypto Wallet - po emailu | http://localhost:8765/crypto-wallet/email?email=user@soas.com | GET |
| Crypto Wallet - dodaj | http://localhost:8765/crypto-wallet | POST |
| Crypto Wallet - azuriraj | http://localhost:8765/crypto-wallet | PUT |
| Crypto Wallet - obrisi | http://localhost:8765/crypto-wallet?email=user@soas.com | DELETE |
| Crypto Exchange | http://localhost:8765/crypto-exchange?from=BTC&to=USD | GET |

## Kredencijali

### API Gateway (Basic Auth)

| Korisnik | Email | Lozinka | Uloga |
|----------|-------|---------|-------|
| Owner | owner@soas.com | owner123 | OWNER |
| Admin | admin@soas.com | admin123 | ADMIN |
| User | user@soas.com | user123 | USER |

## Portovi mikroservisa

| Mikroservis | Port |
|-------------|------|
| Naming Server (Eureka) | 8761 |
| API Gateway | 8765 |
| Users Service | 8770 |
| Currency Exchange | 8000 |
| Currency Conversion | 8100 |
| Bank Account | 8200 |
| Crypto Wallet | 8300 |
| Crypto Exchange | 8400 |
| Trade Service | 8600 |

## Tehnologije
- Java 17
- Spring Boot 4.1.0
- Spring Cloud (Eureka, Gateway, OpenFeign)
- H2 In-Memory Database
- Resilience4J (Circuit Breaker)
- Maven