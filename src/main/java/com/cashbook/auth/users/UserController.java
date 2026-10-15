package com.cashbook.auth.users;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @PostMapping()
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody RegistrationDto requestDto){
        var response = service.registerUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/verify")
    public ResponseEntity<String > verify(@RequestParam String token) {
        return ResponseEntity.ok(service.verify(token));
    }
    @GetMapping("/count")
    public Map<String, Long> getTotalUsers() {
        return Map.of("totalUsers", service.getTotalUsers());
    }

    @GetMapping("/active/count")
    public Map<String, Long> getActiveUsers() {
        return Map.of("activeUsers", service.getActiveUsers());
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<?> handleUserExists(
            UserExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }
    @ExceptionHandler(PhoneNumberExistsException.class)
    public ResponseEntity<?> handleCredentialsDontMatch (){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already registered.");
    }
}

