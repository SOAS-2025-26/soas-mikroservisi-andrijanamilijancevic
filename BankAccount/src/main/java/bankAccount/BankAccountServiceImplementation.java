package bankAccount;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import serviceLibrary.dto.bankAccount.BankAccountDto;
import serviceLibrary.dto.usersService.UserDto;
import serviceLibrary.proxies.UsersServiceProxy;
import serviceLibrary.services.bankAccount.BankAccountService;

@RestController
public class BankAccountServiceImplementation implements BankAccountService {

    @Autowired
    private BankAccountRepository repo;
    
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UsersServiceProxy usersServiceProxy;

    private final ObjectMapper mapper = new ObjectMapper();

    public BankAccountDto modelToDto(BankAccountModel model) {
        return new BankAccountDto(model.getEmail(), model.getCurrencyCode(), model.getAmount());
    }

    private boolean isOwner(String role) {
        return "OWNER".equalsIgnoreCase(role);
    }

    @Override
    public ResponseEntity<?> getAllAccounts() {
        String callerRole = request.getHeader("X-User-Role");
        if (isOwner(callerRole) || !"ADMIN".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can view all accounts.");
        }
        
        List<BankAccountDto> dtos = new ArrayList<>();
        for (BankAccountModel m : repo.findAll()) {
            dtos.add(modelToDto(m));
        }
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<?> getAccountsByEmail(String email) {
        String callerRole = request.getHeader("X-User-Role");
        String callerEmail = request.getHeader("X-User-Email");
        
        if (isOwner(callerRole)) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        if ("USER".equalsIgnoreCase(callerRole) && !email.equalsIgnoreCase(callerEmail)) {
            return ResponseEntity.status(403).body("Not authorized to view another user's account!");
        }
        
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
        String callerRole = request.getHeader("X-User-Role");
        String callerEmail = request.getHeader("X-User-Email");
        
        if (isOwner(callerRole)) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        if ("USER".equalsIgnoreCase(callerRole) && !email.equalsIgnoreCase(callerEmail)) {
            return ResponseEntity.status(403).body("Not authorized to view another user's account!");
        }
        
        BankAccountModel model = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(email, currencyCode);
        if (model == null) {
            return ResponseEntity.status(404)
                    .body("Account with email: " + email + " and currency: " + currencyCode + " not found!");
        }
        return ResponseEntity.ok(modelToDto(model));
    }

    @Override
    public ResponseEntity<?> createAccount(BankAccountDto body) {
        String callerRole = request.getHeader("X-User-Role");
        // Dozvoljeno: ADMIN (ručno kreiranje) i OWNER/null (interni poziv iz UsersService)
        if ("USER".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        UserDto user;
        try {
            user = mapper.convertValue(
                    usersServiceProxy.getUserByEmail(body.getEmail()).getBody(), UserDto.class);
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404)
                    .body("No user with email: " + body.getEmail() + " exists in the system!");
        }
        if (!"USER".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(400)
                    .body("Bank accounts can only be created for users with role USER!");
        }

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
        String callerRole = request.getHeader("X-User-Role");
        if (isOwner(callerRole) || !"ADMIN".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can update accounts.");
        }
        
        BankAccountModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Account with email: " + body.getEmail()
                            + " and currency: " + body.getCurrencyCode() + " not found!");
        }
        existing.setAmount(body.getAmount());
        repo.save(existing);
        return ResponseEntity.ok(modelToDto(existing));
    }

    @Override
    public ResponseEntity<?> debitAccount(BankAccountDto body) {
        // Interni pozivi nemaju uvek striktnu spoljnu autorizaciju, ali za svaki slučaj puštamo
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
        existing.setAmount(existing.getAmount() - body.getAmount());
        repo.save(existing);
        return ResponseEntity.ok(modelToDto(existing));
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

        existing.setAmount(existing.getAmount() + body.getAmount());
        repo.save(existing);
        return ResponseEntity.ok(modelToDto(existing));
    }

    @Override
    public ResponseEntity<?> deleteAccount(String email) {
        // Dozvoljeno za ADMIN-a i za interne pozive (kada OWNER briše korisnika u UsersService)
        String callerRole = request.getHeader("X-User-Role");
        if ("USER".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        
        List<BankAccountModel> existing = repo.findByEmailIgnoreCase(email);
        if (existing.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No bank accounts found for email: " + email);
        }
        repo.deleteByEmail(email);
        return ResponseEntity.ok("All bank accounts for email: " + email + " successfully deleted!");
    }
}