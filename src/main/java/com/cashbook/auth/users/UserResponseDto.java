package com.cashbook.auth.users;

import lombok.Data;

@Data
public class UserResponseDto {
    private Integer id;
    private String username;
    private String phoneNumber;
}
