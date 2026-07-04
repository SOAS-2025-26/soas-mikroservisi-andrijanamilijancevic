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
        List<BankAccountModel> models = repo.findAll();
        List<BankAccountDto> dtos = new ArrayList<>();
        if (!models.isEmpty()) {
            for (BankAccountModel m : models) {
                dtos.add(modelToDto(m));
            }
            return ResponseEntity.ok(dtos);
        }
        return ResponseEntity.status(404).body("Currently no bank accounts in database");
    }

    @Override
    public ResponseEntity<?> getAccountByEmail(String email) {
        BankAccountModel model = repo.findByEmailIgnoreCase(email);
        if (model != null) {
            return ResponseEntity.ok(modelToDto(model));
        }
        return ResponseEntity.status(404)
                .body("Bank account with email: " + email + " not found!");
    }

    @Override
    public ResponseEntity<?> createAccount(BankAccountDto body) {
        if (repo.findByEmailIgnoreCase(body.getEmail()) != null) {
            return ResponseEntity.status(409)
                    .body("Bank account with email: " + body.getEmail() + " already exists!");
        }
        BankAccountModel newAccount = new BankAccountModel(
                body.getEmail(), body.getCurrencyCode(), body.getAmount());
        repo.save(newAccount);
        return ResponseEntity.ok(modelToDto(newAccount));
    }

    @Override
    public ResponseEntity<?> updateAccount(BankAccountDto body) {
        BankAccountModel existing = repo.findByEmailIgnoreCase(body.getEmail());
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Bank account with email: " + body.getEmail() + " not found!");
        }
        repo.updateAccount(body.getEmail(), body.getCurrencyCode(), body.getAmount());
        return ResponseEntity.ok(modelToDto(repo.findByEmailIgnoreCase(body.getEmail())));
    }

    @Override
    public ResponseEntity<?> deleteAccount(String email) {
        BankAccountModel existing = repo.findByEmailIgnoreCase(email);
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Bank account with email: " + email + " not found!");
        }
        repo.deleteByEmail(email);
        return ResponseEntity.ok("Bank account with email: " + email + " successfully deleted!");
    }
}