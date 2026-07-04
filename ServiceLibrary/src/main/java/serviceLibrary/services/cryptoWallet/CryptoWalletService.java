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
    ResponseEntity<?> getWalletByEmail(@RequestParam String email);

    @PostMapping("/crypto-wallet")
    ResponseEntity<?> createWallet(@RequestBody CryptoWalletDto body);

    @PutMapping("/crypto-wallet")
    ResponseEntity<?> updateWallet(@RequestBody CryptoWalletDto body);

    @DeleteMapping("/crypto-wallet")
    ResponseEntity<?> deleteWallet(@RequestParam String email);
}