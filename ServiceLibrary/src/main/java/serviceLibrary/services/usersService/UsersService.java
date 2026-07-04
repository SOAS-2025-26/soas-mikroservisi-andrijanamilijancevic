package serviceLibrary.services.usersService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import serviceLibrary.dto.usersService.UserDto;

@Service
public interface UsersService {

    @GetMapping("/users")
    ResponseEntity<?> getAllUsers();

    @GetMapping("/users/email")
    ResponseEntity<?> getUserByEmail(@RequestParam String email);

    @PostMapping("/users")
    ResponseEntity<?> createUser(@RequestBody UserDto body);

    @PutMapping("/users")
    ResponseEntity<?> updateUser(@RequestBody UserDto body);

    @DeleteMapping("/users")
    ResponseEntity<?> deleteUser(@RequestParam String email);
}