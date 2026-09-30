package com.cashbook.auth;

import com.cashbook.auth.users.Role;
import lombok.Data;

@Data
public class CurrentUserResponseDto {
    private Long id;
    private String username;
    private String phoneNumber;
    private String email;
    private Role role;
}
