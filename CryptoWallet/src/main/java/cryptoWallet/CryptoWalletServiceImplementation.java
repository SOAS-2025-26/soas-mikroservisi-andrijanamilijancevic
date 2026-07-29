package cryptoWallet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import serviceLibrary.dto.cryptoWallet.CryptoWalletDto;
import serviceLibrary.services.cryptoWallet.CryptoWalletService;

@RestController
public class CryptoWalletServiceImplementation implements CryptoWalletService {

    @Autowired
    private CryptoWalletRepository repo;
    
    @Autowired
    private HttpServletRequest request;

    public CryptoWalletDto modelToDto(CryptoWalletModel model) {
        return new CryptoWalletDto(model.getEmail(), model.getCurrencyCode(), model.getAmount());
    }

    private boolean isOwner(String role) {
        return "OWNER".equalsIgnoreCase(role);
    }

    @Override
    public ResponseEntity<?> getAllWallets() {
        String callerRole = request.getHeader("X-User-Role");
        if (isOwner(callerRole) || !"ADMIN".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can view all wallets.");
        }
        
        List<CryptoWalletDto> dtos = new ArrayList<>();
        for (CryptoWalletModel m : repo.findAll()) {
            dtos.add(modelToDto(m));
        }
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<?> getWalletsByEmail(String email) {
        String callerRole = request.getHeader("X-User-Role");
        String callerEmail = request.getHeader("X-User-Email");
        
        if (isOwner(callerRole)) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        if ("USER".equalsIgnoreCase(callerRole) && !email.equalsIgnoreCase(callerEmail)) {
            return ResponseEntity.status(403).body("Not authorized to view another user's wallet!");
        }
        
        List<CryptoWalletModel> models = repo.findByEmailIgnoreCase(email);
        if (models.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No crypto wallets found for email: " + email);
        }
        List<CryptoWalletDto> dtos = new ArrayList<>();
        for (CryptoWalletModel m : models) {
            dtos.add(modelToDto(m));
        }
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<?> getWalletByEmailAndCurrency(String email, String currencyCode) {
        String callerRole = request.getHeader("X-User-Role");
        String callerEmail = request.getHeader("X-User-Email");
        
        if (isOwner(callerRole)) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        if ("USER".equalsIgnoreCase(callerRole) && !email.equalsIgnoreCase(callerEmail)) {
            return ResponseEntity.status(403).body("Not authorized to view another user's wallet!");
        }
        
        CryptoWalletModel model = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(email, currencyCode);
        if (model == null) {
            return ResponseEntity.status(404)
                    .body("Wallet with email: " + email + " and currency: " + currencyCode + " not found!");
        }
        return ResponseEntity.ok(modelToDto(model));
    }

    @Override
    public ResponseEntity<?> createWallet(CryptoWalletDto body) {
        String callerRole = request.getHeader("X-User-Role");
        // Dozvoljeno: ADMIN (ručno kreiranje) i OWNER/null (interni poziv iz UsersService)
        if ("USER".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        
        CryptoWalletModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        if (existing != null) {
            return ResponseEntity.status(409)
                    .body("Crypto wallet with email: " + body.getEmail()
                            + " and currency: " + body.getCurrencyCode() + " already exists!");
        }
        CryptoWalletModel newWallet = new CryptoWalletModel(
                body.getEmail(), body.getCurrencyCode(), body.getAmount());
        repo.save(newWallet);
        return ResponseEntity.ok(modelToDto(newWallet));
    }

    @Override
    public ResponseEntity<?> updateWallet(CryptoWalletDto body) {
        String callerRole = request.getHeader("X-User-Role");
        if (isOwner(callerRole) || !"ADMIN".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied. Only ADMIN can update wallets.");
        }
        
        CryptoWalletModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Wallet with email: " + body.getEmail()
                            + " and currency: " + body.getCurrencyCode() + " not found!");
        }
        existing.setAmount(body.getAmount());
        repo.save(existing);
        return ResponseEntity.ok(modelToDto(existing));
    }

    @Override
    public ResponseEntity<?> debitWallet(CryptoWalletDto body) {
        CryptoWalletModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Wallet with email: " + body.getEmail()
                            + " and currency: " + body.getCurrencyCode() + " not found!");
        }
        if (existing.getAmount() < body.getAmount()) {
            return ResponseEntity.status(400)
                    .body("Insufficient " + existing.getCurrencyCode() + " in crypto wallet! Available: "
                            + existing.getAmount() + ", requested: " + body.getAmount());
        }
        existing.setAmount(existing.getAmount() - body.getAmount());
        repo.save(existing);
        return ResponseEntity.ok(modelToDto(existing));
    }

    @Override
    public ResponseEntity<?> creditWallet(CryptoWalletDto body) {
        CryptoWalletModel existing = repo.findByEmailIgnoreCaseAndCurrencyCodeIgnoreCase(
                body.getEmail(), body.getCurrencyCode());

        if (existing == null) {
            CryptoWalletModel newWallet = new CryptoWalletModel(
                    body.getEmail(), body.getCurrencyCode(), body.getAmount());
            repo.save(newWallet);
            return ResponseEntity.ok(modelToDto(newWallet));
        }

        existing.setAmount(existing.getAmount() + body.getAmount());
        repo.save(existing);
        return ResponseEntity.ok(modelToDto(existing));
    }

    @Override
    public ResponseEntity<?> deleteWallet(String email) {
        String callerRole = request.getHeader("X-User-Role");
        if ("USER".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        
        List<CryptoWalletModel> existing = repo.findByEmailIgnoreCase(email);
        if (existing.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No crypto wallets found for email: " + email);
        }
        repo.deleteByEmail(email);
        return ResponseEntity.ok("All crypto wallets for email: " + email + " successfully deleted!");
    }
}