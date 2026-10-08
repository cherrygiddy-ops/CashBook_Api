package com.cashbook.business;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessRequestDto {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @Email(message = "Invalid email address")
    private String email;

    private String phoneNumber;

    private String address;
}
