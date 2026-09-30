package com.cashbook.auth;

import com.cashbook.auth.jwt.Jwt;
import com.cashbook.auth.jwt.JwtConfig;
import com.cashbook.auth.jwt.JwtService;
import com.cashbook.auth.users.User;
import com.cashbook.auth.users.UserNotFoundException;
import com.cashbook.auth.users.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository repository;
    private final JwtConfig config;
    private final AuthenticationManager authenticationManager;
    private final JwtService service;
    private  final LoginMapper loginMapper;

    public User getCurrentUser(){
        var userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
         return repository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
    public CurrentUserResponseDto getCurrentUserResponse() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if ("anonymousUser".equals(principal)) {
            throw new UserNotFoundException();
        }

        String userId = (String) principal;
        var user = repository.findById(Long.valueOf(userId))
                .orElseThrow(UserNotFoundException::new);

        return loginMapper.toDto(user);


    }



    public Jwt getJwt(LoginRequestDto requestDto, HttpServletResponse response) throws AccountNotVerifiedException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));

        var user =repository.findByEmail(requestDto.getEmail()).orElseThrow(UserNotFoundException::new);

        if (!user.isVerified()) {
            throw new AccountNotVerifiedException();
        }


        var token =service.generateAccessTokens(user);
        var refreshToken = service.generateRefreshTokens(user);

        String refreshTokenValue = refreshToken.toString();
        String cookieString = String.format(
                "refreshToken=%s; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=%d",
                refreshTokenValue,
                config.getRefreshExpiration()
        );
        response.addHeader("Set-Cookie", cookieString);

        response.addHeader("Set-Cookie", cookieString);

        return token;

    }

    public Jwt refreshToken(String refreshToken) {
        var jwt = service.parseToken(refreshToken);
        if (jwt==null || jwt.isExpired())
            throw new InvalidTokenException();
        var userId = jwt.getUserId();
        var user = repository.findById(Long.valueOf(userId)).orElse(null);
        if (user == null)
            throw  new UserNotFoundException();
        return service.generateAccessTokens(user);

    }
}
