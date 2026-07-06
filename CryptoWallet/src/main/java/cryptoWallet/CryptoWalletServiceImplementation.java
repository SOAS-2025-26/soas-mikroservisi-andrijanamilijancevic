package cryptoWallet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import serviceLibrary.dto.cryptoWallet.CryptoWalletDto;
import serviceLibrary.services.cryptoWallet.CryptoWalletService;

@RestController
public class CryptoWalletServiceImplementation implements CryptoWalletService {

    @Autowired
    private CryptoWalletRepository repo;

    public CryptoWalletDto modelToDto(CryptoWalletModel model) {
        return new CryptoWalletDto(model.getEmail(), model.getCurrencyCode(), model.getAmount());
    }

    @Override
    public ResponseEntity<?> getAllWallets() {
        List<CryptoWalletModel> models = repo.findAll();
        List<CryptoWalletDto> dtos = new ArrayList<>();
        if (!models.isEmpty()) {
            for (CryptoWalletModel m : models) {
                dtos.add(modelToDto(m));
            }
            return ResponseEntity.ok(dtos);
        }
        return ResponseEntity.ok(dtos);
        }

    @Override
    public ResponseEntity<?> getWalletByEmail(String email) {
        CryptoWalletModel model = repo.findByEmailIgnoreCase(email);
        if (model != null) {
            return ResponseEntity.ok(modelToDto(model));
        }
        return ResponseEntity.status(404)
                .body("Crypto wallet with email: " + email + " not found!");
    }

    @Override
    public ResponseEntity<?> createWallet(CryptoWalletDto body) {
        if (repo.findByEmailIgnoreCase(body.getEmail()) != null) {
            return ResponseEntity.status(409)
                    .body("Crypto wallet with email: " + body.getEmail() + " already exists!");
        }
        CryptoWalletModel newWallet = new CryptoWalletModel(
                body.getEmail(), body.getCurrencyCode(), body.getAmount());
        repo.save(newWallet);
        return ResponseEntity.ok(modelToDto(newWallet));
    }

    @Override
    public ResponseEntity<?> updateWallet(CryptoWalletDto body) {
        CryptoWalletModel existing = repo.findByEmailIgnoreCase(body.getEmail());
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Crypto wallet with email: " + body.getEmail() + " not found!");
        }
        repo.updateWallet(body.getEmail(), body.getCurrencyCode(), body.getAmount());
        return ResponseEntity.ok(modelToDto(repo.findByEmailIgnoreCase(body.getEmail())));
    }

    @Override
    public ResponseEntity<?> deleteWallet(String email) {
        CryptoWalletModel existing = repo.findByEmailIgnoreCase(email);
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("Crypto wallet with email: " + email + " not found!");
        }
        repo.deleteByEmail(email);
        return ResponseEntity.ok("Crypto wallet with email: " + email + " successfully deleted!");
    }
}