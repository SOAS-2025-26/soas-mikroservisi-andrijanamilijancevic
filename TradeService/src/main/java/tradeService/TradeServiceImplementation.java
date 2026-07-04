package tradeService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;
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
        CryptoWalletDto wallet;
        try {
            wallet = mapper.convertValue(
                    cryptoWalletProxy.getWalletByEmail(email).getBody(), CryptoWalletDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("Crypto wallet not found!");
        }

        if (!wallet.getCurrencyCode().equalsIgnoreCase(from) || wallet.getAmount() < quantity) {
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
        cryptoWalletProxy.updateWallet(
                new CryptoWalletDto(wallet.getEmail(), to, wallet.getAmount() - quantity + toAmount));

        String message = "Successfully exchanged " + from + ": " + quantity
                + " for " + to + ": " + toAmount;
        return ResponseEntity.ok(new TradeResponseDto(message,
                new CryptoWalletDto(wallet.getEmail(), to, toAmount)));
    }

    // FIAT -> CRYPTO
    private ResponseEntity<?> fiatToCrypto(String from, String to, double quantity, String email) {
        BankAccountDto account;
        try {
            account = mapper.convertValue(
                    bankAccountProxy.getAccountByEmail(email).getBody(), BankAccountDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("Bank account not found!");
        }

        String baseCurrency = from;
        double baseQuantity = quantity;

        if (!from.equals("USD") && !from.equals("EUR")) {
            try {
                CurrencyConversionDto converted = mapper.convertValue(
                        currencyConversionProxy.currencyConversion(from, "USD", quantity).getBody(),
                        CurrencyConversionDto.class);
                baseCurrency = "USD";
                baseQuantity = converted.getExchangedAmount();
            } catch (FeignException e) {
                return ResponseEntity.status(400).body("Could not convert " + from + " to USD!");
            }
        }

        if (!account.getCurrencyCode().equalsIgnoreCase(baseCurrency)
                || account.getAmount() < baseQuantity) {
            return ResponseEntity.status(400)
                    .body("Insufficient " + baseCurrency + " in bank account!");
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

        bankAccountProxy.updateAccount(
                new BankAccountDto(account.getEmail(), baseCurrency,
                        account.getAmount() - baseQuantity));

        CryptoWalletDto wallet;
        try {
            wallet = mapper.convertValue(
                    cryptoWalletProxy.getWalletByEmail(email).getBody(), CryptoWalletDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("Crypto wallet not found!");
        }

        cryptoWalletProxy.updateWallet(
                new CryptoWalletDto(wallet.getEmail(), to,
                        wallet.getAmount() + cryptoAmount));

        String message = "Successfully exchanged " + baseCurrency + ": " + baseQuantity
                + " for " + to + ": " + cryptoAmount;
        return ResponseEntity.ok(new TradeResponseDto(message,
                new CryptoWalletDto(wallet.getEmail(), to, wallet.getAmount() + cryptoAmount)));
    }

    // CRYPTO -> FIAT
    private ResponseEntity<?> cryptoToFiat(String from, String to, double quantity, String email) {
        CryptoWalletDto wallet;
        try {
            wallet = mapper.convertValue(
                    cryptoWalletProxy.getWalletByEmail(email).getBody(), CryptoWalletDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("Crypto wallet not found!");
        }

        if (!wallet.getCurrencyCode().equalsIgnoreCase(from) || wallet.getAmount() < quantity) {
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

        if (!to.equals("USD") && !to.equals("EUR")) {
            try {
                CurrencyConversionDto converted = mapper.convertValue(
                        currencyConversionProxy.currencyConversion(targetFiat, to, fiatAmount).getBody(),
                        CurrencyConversionDto.class);
                fiatAmount = converted.getExchangedAmount();
            } catch (FeignException e) {
                return ResponseEntity.status(400).body("Could not convert to " + to + "!");
            }
        }

        cryptoWalletProxy.updateWallet(
                new CryptoWalletDto(wallet.getEmail(), from,
                        wallet.getAmount() - quantity));

        BankAccountDto account;
        try {
            account = mapper.convertValue(
                    bankAccountProxy.getAccountByEmail(email).getBody(), BankAccountDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body("Bank account not found!");
        }

        bankAccountProxy.updateAccount(
                new BankAccountDto(account.getEmail(), to,
                        account.getAmount() + fiatAmount));

        String message = "Successfully exchanged " + from + ": " + quantity
                + " for " + to + ": " + fiatAmount;
        return ResponseEntity.ok(new TradeResponseDto(message,
                new BankAccountDto(account.getEmail(), to, account.getAmount() + fiatAmount)));
    }
}