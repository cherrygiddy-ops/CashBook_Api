package com.cashbook.auth.users;

import com.cashbook.auth.InvalidTokenException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public UserResponseDto registerUser(RegistrationDto requestDto)  {
        if (repository.existsByEmail(requestDto.getEmail()))
            throw new UserExistsException();
        var user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ADMIN);
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationtokenExpiry(LocalDateTime.now().plusHours(24));
        user.setVerified(true);
        repository.save(user);
        //emailService.sendVerificationEmail(email, token);
        return userMapper.toDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        var users = repository.findAll();

        return users.stream().map(userMapper::toDto).toList();
    }


    public String verify(String token) {
        var user = repository.findByVerificationToken(token).orElseThrow(UserNotFoundException::new);
        if (user.getVerificationtokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException();
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationtokenExpiry(null);
        repository.save(user);

        return "Account verified successfully";
    }


    public Long getTotalUsers() {
        return repository.countAllUsers();
    }

    public Long getActiveUsers() {
        return repository.countActiveUsers();
    }
}
