package tradeService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import serviceLibrary.dto.bankAccount.BankAccountDto;
import serviceLibrary.dto.cryptoExchange.CryptoExchangeDto;
import serviceLibrary.dto.cryptoWallet.CryptoWalletDto;
import serviceLibrary.dto.currencyConversion.CurrencyConversionDto;
import serviceLibrary.dto.tradeService.TradeResponseDto;
import serviceLibrary.proxies.BankAccountProxy;
import serviceLibrary.proxies.CryptoExchangeProxy;
import serviceLibrary.proxies.CryptoWalletProxy;
import serviceLibrary.proxies.CurrencyConversionProxy;
import serviceLibrary.services.tradeService.TradeService;

import java.util.Set;

@RestController
public class TradeServiceImplementation implements TradeService {

    private static final Set<String> CRYPTO_CODES = Set.of(
            "BTC", "ETH", "SOL", "ADA", "DOGE", "XRP", "DOT", "LTC"
    );

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private BankAccountProxy bankAccountProxy;

    @Autowired
    private CryptoWalletProxy cryptoWalletProxy;

    @Autowired
    private CryptoExchangeProxy cryptoExchangeProxy;

    @Autowired
    private CurrencyConversionProxy currencyConversionProxy;

    @Override
    @CircuitBreaker(name = "tradeService", fallbackMethod = "tradeFallback")
    public ResponseEntity<?> trade(String from, String to, double quantity, String email) {
        boolean fromIsCrypto = CRYPTO_CODES.contains(from.toUpperCase());
        boolean toIsCrypto = CRYPTO_CODES.contains(to.toUpperCase());

        if (fromIsCrypto && toIsCrypto) {
            return cryptoToCrypto(from.toUpperCase(), to.toUpperCase(), quantity, email);
        } else if (!fromIsCrypto && toIsCrypto) {
            return fiatToCrypto(from.toUpperCase(), to.toUpperCase(), quantity, email);
        } else if (fromIsCrypto && !toIsCrypto) {
            return cryptoToFiat(from.toUpperCase(), to.toUpperCase(), quantity, email);
        } else {
            return ResponseEntity.status(400)
                    .body("Use /currency-conversion for fiat to fiat exchange!");
        }
    }

    // CRYPTO -> CRYPTO
    private ResponseEntity<?> cryptoToCrypto(String from, String to, double quantity, String email) {
        CryptoWalletDto sourceWallet;
        try {
            sourceWallet = mapper.convertValue(
                    cryptoWalletProxy.getWalletByEmailAndCurrency(email, from).getBody(), CryptoWalletDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("You don't have a " + from + " wallet balance!");
        }

        if (sourceWallet.getAmount() < quantity) {
            return ResponseEntity.status(400)
                    .body("Insufficient " + from + " in crypto wallet!");
        }

        CryptoExchangeDto rate;
        try {
            rate = mapper.convertValue(
                    cryptoExchangeProxy.getExchangeRate(from, to).getBody(), CryptoExchangeDto.class);
        } catch (FeignException e) {
            return ResponseEntity.status(400).body("Could not fetch exchange rate!");
        }

        double toAmount = quantity * rate.getRate();

        cryptoWalletProxy.debitWallet(new CryptoWalletDto(email, from, quantity));
        CryptoWalletDto updatedTarget = mapper.convertValue(
                cryptoWalletProxy.creditWallet(new CryptoWalletDto(email, to, toAmount)).getBody(),
                CryptoWalletDto.class);

        String message = "Successfully exchanged " + from + ": " + quantity
                + " for " + to + ": " + toAmount;
        return ResponseEntity.ok(new TradeResponseDto(message, updatedTarget));
    }

    // FIAT -> CRYPTO
    private ResponseEntity<?> fiatToCrypto(String from, String to, double quantity, String email) {
        String baseCurrency = from;
        double baseQuantity = quantity;

        if (!from.equals("USD") && !from.equals("EUR")) {
            try {
                CurrencyConversionDto converted = mapper.convertValue(
                        currencyConversionProxy.currencyConversion(from, "USD", quantity, email).getBody(),
                        CurrencyConversionDto.class);
                baseCurrency = "USD";
                baseQuantity = converted.getExchangedAmount();
            } catch (FeignException e) {
                return ResponseEntity.status(400).body("Could not convert " + from + " to USD!");
            }
        } else {
            BankAccountDto account;
            try {
                account = mapper.convertValue(
                        bankAccountProxy.getAccountByEmailAndCurrency(email, baseCurrency).getBody(),
                        BankAccountDto.class);
            } catch (FeignException.NotFound e) {
                return ResponseEntity.status(404).body("You don't have a " + baseCurrency + " balance!");
            }
            if (account.getAmount() < baseQuantity) {
                return ResponseEntity.status(400)
                        .body("Insufficient " + baseCurrency + " in bank account!");
            }
        }

        CryptoExchangeDto rate;
        try {
            rate = mapper.convertValue(
                    cryptoExchangeProxy.getExchangeRate(to, baseCurrency).getBody(),
                    CryptoExchangeDto.class);
        } catch (FeignException e) {
            return ResponseEntity.status(400).body("Could not fetch exchange rate!");
        }

        double cryptoAmount = baseQuantity / rate.getRate();

        // ako je konverzija iz nefiat-USD/EUR vec izvrsena, currency-conversion je vec skinuo novac;
        // ovde skidamo iz baseCurrency samo ako original "from" IS USD/EUR (nije vec skinuto)
        if (from.equals("USD") || from.equals("EUR")) {
            bankAccountProxy.debitAccount(new BankAccountDto(email, baseCurrency, baseQuantity));
        }

        CryptoWalletDto updatedWallet = mapper.convertValue(
                cryptoWalletProxy.creditWallet(new CryptoWalletDto(email, to, cryptoAmount)).getBody(),
                CryptoWalletDto.class);

        String message = "Successfully exchanged " + baseCurrency + ": " + baseQuantity
                + " for " + to + ": " + cryptoAmount;
        return ResponseEntity.ok(new TradeResponseDto(message, updatedWallet));
    }

    // CRYPTO -> FIAT
    private ResponseEntity<?> cryptoToFiat(String from, String to, double quantity, String email) {
        CryptoWalletDto wallet;
        try {
            wallet = mapper.convertValue(
                    cryptoWalletProxy.getWalletByEmailAndCurrency(email, from).getBody(), CryptoWalletDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("You don't have a " + from + " wallet balance!");
        }

        if (wallet.getAmount() < quantity) {
            return ResponseEntity.status(400)
                    .body("Insufficient " + from + " in crypto wallet!");
        }

        String targetFiat = (to.equals("USD") || to.equals("EUR")) ? to : "USD";

        CryptoExchangeDto rate;
        try {
            rate = mapper.convertValue(
                    cryptoExchangeProxy.getExchangeRate(from, targetFiat).getBody(),
                    CryptoExchangeDto.class);
        } catch (FeignException e) {
            return ResponseEntity.status(400).body("Could not fetch exchange rate!");
        }

        double fiatAmount = quantity * rate.getRate();

        cryptoWalletProxy.debitWallet(new CryptoWalletDto(email, from, quantity));

        BankAccountDto updatedAccount;
        if (!to.equals("USD") && !to.equals("EUR")) {
            // prvo kreditujemo USD, pa currency-conversion prebacuje u zeljenu valutu
            bankAccountProxy.creditAccount(new BankAccountDto(email, targetFiat, fiatAmount));
            try {
                CurrencyConversionDto converted = mapper.convertValue(
                        currencyConversionProxy.currencyConversion(targetFiat, to, fiatAmount, email).getBody(),
                        CurrencyConversionDto.class);
                updatedAccount = converted.getAccountState();
            } catch (FeignException e) {
                return ResponseEntity.status(400).body("Could not convert to " + to + "!");
            }
        } else {
            updatedAccount = mapper.convertValue(
                    bankAccountProxy.creditAccount(new BankAccountDto(email, to, fiatAmount)).getBody(),
                    BankAccountDto.class);
        }

        String message = "Successfully exchanged " + from + ": " + quantity
                + " for " + to + ": " + fiatAmount;
        return ResponseEntity.ok(new TradeResponseDto(message, updatedAccount));
    }

    public ResponseEntity<?> tradeFallback(String from, String to, double quantity, String email, Exception e) {
        return ResponseEntity.status(503)
                .body("Trade service is currently unavailable. Please try again later. Error: " + e.getMessage());
    }
}