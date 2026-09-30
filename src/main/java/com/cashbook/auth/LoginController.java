package com.cashbook.auth;

import com.cashbook.auth.jwt.JwtResponseDto;
import com.cashbook.auth.users.UserNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid  @RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        try {
           var  token = authService.getJwt(requestDto, response);
        return ResponseEntity.ok().body(new JwtResponseDto(token.toString()));
        } catch (AccountNotVerifiedException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue (value = "refreshToken") String refreshToken){
        System.out.println("Received refreshToken: " + refreshToken);
        var token=authService.refreshToken(refreshToken);
       return  ResponseEntity.ok(new JwtResponseDto(token.toString()));
    }

    @GetMapping("/currentUser")
    public CurrentUserResponseDto getCurrentUserResponse(){
        return authService.getCurrentUserResponse();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound (){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleCredentialsDontMatch (){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credentials Not Matching");
    }

    @ExceptionHandler(CredentialsMatchExceptions.class)
    public ResponseEntity<?> handleCredentialMatch (){
        return ResponseEntity.status(HttpStatus.OK).body("Authorized");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenMatch (){
        return ResponseEntity.status(HttpStatus.OK).body("invalid Tokens");
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<?> handleAccountNotVerified (){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please verify your email before logging in.");
    }

}
