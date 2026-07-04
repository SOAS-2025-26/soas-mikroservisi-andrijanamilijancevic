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

    @GetMapping("/bank-account/email")
    ResponseEntity<?> getAccountByEmail(@RequestParam String email);

    @PostMapping("/bank-account")
    ResponseEntity<?> createAccount(@RequestBody BankAccountDto body);

    @PutMapping("/bank-account")
    ResponseEntity<?> updateAccount(@RequestBody BankAccountDto body);

    @DeleteMapping("/bank-account")
    ResponseEntity<?> deleteAccount(@RequestParam String email);
}