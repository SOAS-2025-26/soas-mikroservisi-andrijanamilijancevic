package serviceLibrary.services.cryptoWallet;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import serviceLibrary.dto.cryptoWallet.CryptoWalletDto;

@Service
public interface CryptoWalletService {

    @GetMapping("/crypto-wallet")
    ResponseEntity<?> getAllWallets();

    @GetMapping("/crypto-wallet/email")
    ResponseEntity<?> getWalletsByEmail(@RequestParam String email);

    @GetMapping("/crypto-wallet/email-currency")
    ResponseEntity<?> getWalletByEmailAndCurrency(@RequestParam String email,
                                                   @RequestParam String currencyCode);

    @PostMapping("/crypto-wallet")
    ResponseEntity<?> createWallet(@RequestBody CryptoWalletDto body);

    @PutMapping("/crypto-wallet")
    ResponseEntity<?> updateWallet(@RequestBody CryptoWalletDto body);

    @PutMapping("/crypto-wallet/debit")
    ResponseEntity<?> debitWallet(@RequestBody CryptoWalletDto body);

    @PutMapping("/crypto-wallet/credit")
    ResponseEntity<?> creditWallet(@RequestBody CryptoWalletDto body);

    @DeleteMapping("/crypto-wallet")
    ResponseEntity<?> deleteWallet(@RequestParam String email);
}