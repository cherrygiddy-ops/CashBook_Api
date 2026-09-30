package com.cashbook.auth.users;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationDto {
    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String password;

}
