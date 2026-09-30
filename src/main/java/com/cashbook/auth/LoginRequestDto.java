package com.cashbook.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "phoneNumber required")
    private String phoneNumber;


    @NotBlank(message = "password required")
    private String password;
}
