package bankAccount;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import serviceLibrary.dto.bankAccount.BankAccountDto;
import serviceLibrary.services.bankAccount.BankAccountService;

@RestController
public class BankAccountServiceImplementation implements BankAccountService {

    @Autowired
    private BankAccountRepository repo;

    public BankAccountDto modelToDto(BankAccountModel model) {
        return new BankAccountDto(model.getEmail(), model.getCurrencyCode(), model.getAmount());
    }

    @Override
    public ResponseEntity<?> getAllAccounts() {
        List<BankAccountDto> dtos = new ArrayList<>();
        for (BankAccountModel m : repo.findAll()) {
            dtos.add(modelToDto(m));
        }
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<?> getAccountsByEmail(String email) {
        List<BankAccountModel> models = repo.findByEmailIgnoreCase(email);
        if (models.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No bank accounts found for email: " + email);
        }
        List<BankAccountDto> dtos = new ArrayList<>();
        for (BankAccountModel m : models) {
            dtos.add(modelToDto(m));
        }
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<?> getAccountByEmailAndCurrency(String email, String currencyCode) {
        BankAccountModel model = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(email, currencyCode);
        if (model == null) {
            return ResponseEntity.status(404)
                    .body("Account with email: " + email + " and currency: " + currencyCode + " not found!");
        }
        return ResponseEntity.ok(modelToDto(model));
    }

    @Override
    public ResponseEntity<?> createAccount(BankAccountDto body) {
        BankAccountModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        if (existing != null) {
            return ResponseEntity.status(409)
                    .body("Bank account with email: " + body.getEmail()
                            + " and currency: " + body.getCurrencyCode() + " already exists!");
        }
        BankAccountModel newAccount = new BankAccountModel(
                body.getEmail(), body.getCurrencyCode(), body.getAmount());
        repo.save(newAccount);
        return ResponseEntity.ok(modelToDto(newAccount));
    }

    @Override
    public ResponseEntity<?> updateAccount(BankAccountDto body) {
        BankAccountModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Account with email: " + body.getEmail()
                            + " and currency: " + body.getCurrencyCode() + " not found!");
        }
        repo.updateAmount(body.getEmail(), body.getCurrencyCode(), body.getAmount());
        BankAccountModel updated = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        return ResponseEntity.ok(modelToDto(updated));
    }

    @Override
    public ResponseEntity<?> debitAccount(BankAccountDto body) {
        BankAccountModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Account with email: " + body.getEmail()
                            + " and currency: " + body.getCurrencyCode() + " not found!");
        }
        if (existing.getAmount() < body.getAmount()) {
            return ResponseEntity.status(400)
                    .body("Insufficient funds! Available: " + existing.getAmount()
                            + " " + existing.getCurrencyCode() + ", requested: " + body.getAmount());
        }
        double newAmount = existing.getAmount() - body.getAmount();
        repo.updateAmount(body.getEmail(), body.getCurrencyCode(), newAmount);

        BankAccountModel updated = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        return ResponseEntity.ok(modelToDto(updated));
    }

    @Override
    public ResponseEntity<?> creditAccount(BankAccountDto body) {
        BankAccountModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());

        if (existing == null) {
            BankAccountModel newAccount = new BankAccountModel(
                    body.getEmail(), body.getCurrencyCode(), body.getAmount());
            repo.save(newAccount);
            return ResponseEntity.ok(modelToDto(newAccount));
        }

        double newAmount = existing.getAmount() + body.getAmount();
        repo.updateAmount(body.getEmail(), body.getCurrencyCode(), newAmount);

        BankAccountModel updated = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        return ResponseEntity.ok(modelToDto(updated));
    }

    @Override
    public ResponseEntity<?> deleteAccount(String email) {
        List<BankAccountModel> existing = repo.findByEmailIgnoreCase(email);
        if (existing.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No bank accounts found for email: " + email);
        }
        repo.deleteByEmail(email);
        return ResponseEntity.ok("All bank accounts for email: " + email + " successfully deleted!");
    }
}