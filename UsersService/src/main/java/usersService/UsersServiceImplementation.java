package usersService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
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
    
    @Autowired
    private HttpServletRequest request;

    public UserDto modelToDto(UserModel model) {
        return new UserDto(model.getEmail(), model.getPassword(), model.getRole());
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        String callerRole = request.getHeader("X-User-Role");
        if (callerRole == null || (!"OWNER".equalsIgnoreCase(callerRole) && !"ADMIN".equalsIgnoreCase(callerRole))) {
            return ResponseEntity.status(403).body("Access denied. Only OWNER or ADMIN can view all users.");
        }

        List<UserModel> models = repo.findAll();
        List<UserDto> dtos = new ArrayList<>();
        for (UserModel m : models) {
            dtos.add(modelToDto(m));
        }
        return ResponseEntity.ok(dtos);
    }

    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        String callerRole = request.getHeader("X-User-Role");
        // Ako nema headera = interni poziv iz auth managera (Gateway) — dozvoli
        if (callerRole != null && !callerRole.isBlank()) {
            if (!"OWNER".equalsIgnoreCase(callerRole) && !"ADMIN".equalsIgnoreCase(callerRole)) {
                return ResponseEntity.status(403).body("Access denied. Only OWNER or ADMIN can view user by email.");
            }
        }

        UserModel model = repo.findByEmailIgnoreCase(email);
        if (model != null) {
            return ResponseEntity.ok(modelToDto(model));
        }
        return ResponseEntity.status(404)
                .body("User with email: " + email + " not found!");
    }

    @Override
    public ResponseEntity<?> createUser(UserDto body) {
        String callerRole = request.getHeader("X-User-Role");
        if (callerRole == null || (!"OWNER".equalsIgnoreCase(callerRole) && !"ADMIN".equalsIgnoreCase(callerRole))) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        if ("ADMIN".equalsIgnoreCase(callerRole) && !"USER".equalsIgnoreCase(body.getRole())) {
            return ResponseEntity.status(403)
                    .body("Admin can only create users with role USER!");
        }
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
                body.getEmail(),
                body.getPassword(),
                body.getRole().toUpperCase());
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
        String callerRole = request.getHeader("X-User-Role");
        if (callerRole == null || (!"OWNER".equalsIgnoreCase(callerRole) && !"ADMIN".equalsIgnoreCase(callerRole))) {
            return ResponseEntity.status(403).body("Access denied.");
        }
        if ("ADMIN".equalsIgnoreCase(callerRole)
                && (!"USER".equalsIgnoreCase(existing.getRole()) || !"USER".equalsIgnoreCase(body.getRole()))) {
            return ResponseEntity.status(403)
                    .body("Admin can only update users with role USER, and cannot change their role!");
        }

        existing.setPassword(body.getPassword());
        existing.setRole(body.getRole().toUpperCase());
        repo.save(existing);

        return ResponseEntity.ok(modelToDto(existing));
    }

    @Override
    public ResponseEntity<?> deleteUser(String email) {
        String callerRole = request.getHeader("X-User-Role");
        if (callerRole == null || !"OWNER".equalsIgnoreCase(callerRole)) {
            return ResponseEntity.status(403).body("Access denied. Only OWNER can delete users.");
        }

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

    @Override
    public ResponseEntity<?> loginUser(String email, String password) {
        UserModel model = repo.findByEmailIgnoreCase(email);
        if (model == null) {
            return ResponseEntity.status(404)
                    .body("User with email: " + email + " not found!");
        }
        if (!model.getPassword().equals(password)) {
            return ResponseEntity.status(401)
                    .body("Invalid password!");
        }
        return ResponseEntity.ok(modelToDto(model));
    }
}