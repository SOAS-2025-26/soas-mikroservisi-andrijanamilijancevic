package usersService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import serviceLibrary.dto.bankAccount.BankAccountDto;
import serviceLibrary.dto.cryptoWallet.CryptoWalletDto;
import serviceLibrary.dto.usersService.UserDto;
import serviceLibrary.proxies.BankAccountProxy;
import serviceLibrary.proxies.CryptoWalletProxy;
import serviceLibrary.services.usersService.UsersService;

@RestController
public class UsersServiceImplementation implements UsersService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private BankAccountProxy bankAccountProxy;

    @Autowired
    private CryptoWalletProxy cryptoWalletProxy;

    private final ObjectMapper mapper = new ObjectMapper();

    public UserDto modelToDto(UserModel model) {
        return new UserDto(model.getEmail(), model.getPassword(), model.getRole());
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        List<UserModel> models = repo.findAll();
        List<UserDto> dtos = new ArrayList<>();
        for (UserModel m : models) {
            dtos.add(modelToDto(m));
        }
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        UserModel model = repo.findByEmailIgnoreCase(email);
        if (model != null) {
            return ResponseEntity.ok(modelToDto(model));
        }
        return ResponseEntity.status(404)
                .body("User with email: " + email + " not found!");
    }

    @Override
    public ResponseEntity<?> createUser(UserDto body) {
        if (repo.findByEmailIgnoreCase(body.getEmail()) != null) {
            return ResponseEntity.status(409)
                    .body("User with email: " + body.getEmail() + " already exists!");
        }
        if (body.getRole().equalsIgnoreCase("OWNER")) {
            boolean ownerExists = repo.findAll()
                    .stream()
                    .anyMatch(u -> u.getRole().equalsIgnoreCase("OWNER"));
            if (ownerExists) {
                return ResponseEntity.status(409)
                        .body("Owner already exists in the system!");
            }
        }

        UserModel newUser = new UserModel(
                body.getEmail(), body.getPassword(), body.getRole().toUpperCase());
        repo.save(newUser);

        if (body.getRole().equalsIgnoreCase("USER")) {
            bankAccountProxy.createAccount(
                    new BankAccountDto(body.getEmail(), "EUR", 0.0));
            cryptoWalletProxy.createWallet(
                    new CryptoWalletDto(body.getEmail(), "ETH", 0.0));
        }

        return ResponseEntity.ok(modelToDto(newUser));
    }

    @Override
    public ResponseEntity<?> updateUser(UserDto body) {
        UserModel existing = repo.findByEmailIgnoreCase(body.getEmail());
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("User with email: " + body.getEmail() + " not found!");
        }
        repo.updateUser(body.getEmail(), body.getPassword(), body.getRole().toUpperCase());
        return ResponseEntity.ok(modelToDto(repo.findByEmailIgnoreCase(body.getEmail())));
    }

    @Override
    public ResponseEntity<?> deleteUser(String email) {
        UserModel existing = repo.findByEmailIgnoreCase(email);
        if (existing == null) {
            return ResponseEntity.status(404)
                    .body("User with email: " + email + " not found!");
        }

        if (existing.getRole().equalsIgnoreCase("USER")) {
            try { bankAccountProxy.deleteAccount(email); } catch (Exception e) {}
            try { cryptoWalletProxy.deleteWallet(email); } catch (Exception e) {}
        }

        repo.delete(existing);
        return ResponseEntity.ok("User with email: " + email + " successfully deleted!");
    }
}