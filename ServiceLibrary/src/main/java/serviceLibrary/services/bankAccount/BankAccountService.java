package serviceLibrary.services.bankAccount;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import serviceLibrary.dto.bankAccount.BankAccountDto;

@Service
public interface BankAccountService {

    @GetMapping("/bank-account")
    ResponseEntity<?> getAllAccounts();

    // Sve valute jednog korisnika
    @GetMapping("/bank-account/email")
    ResponseEntity<?> getAccountsByEmail(@RequestParam String email);

    // Tacno jedna valuta jednog korisnika
    @GetMapping("/bank-account/email-currency")
    ResponseEntity<?> getAccountByEmailAndCurrency(@RequestParam String email,
                                                    @RequestParam String currencyCode);

    @PostMapping("/bank-account")
    ResponseEntity<?> createAccount(@RequestBody BankAccountDto body);

    // Admin - eksplicitno postavljanje apsolutnog iznosa
    @PutMapping("/bank-account")
    ResponseEntity<?> updateAccount(@RequestBody BankAccountDto body);

    // Interno (currency-conversion, trade-service) - umanjuje postojecu valutu
    @PutMapping("/bank-account/debit")
    ResponseEntity<?> debitAccount(@RequestBody BankAccountDto body);

    // Interno (currency-conversion, trade-service) - uvecava ili kreira valutu
    @PutMapping("/bank-account/credit")
    ResponseEntity<?> creditAccount(@RequestBody BankAccountDto body);

    @DeleteMapping("/bank-account")
    ResponseEntity<?> deleteAccount(@RequestParam String email);
}