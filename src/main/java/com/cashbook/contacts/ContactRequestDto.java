package com.cashbook.contacts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequestDto {

    @NotBlank(message = "Contact name is required")
    private String name;

    private String phoneNumber;

    @Email(message = "Invalid email address")
    private String email;

    @NotNull(message = "Business ID is required")
    private Long businessId;
}