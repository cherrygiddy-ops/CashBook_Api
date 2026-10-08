package com.cashbook.auth.users;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationDto {
    @NotNull
    private String username;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String password;

}
