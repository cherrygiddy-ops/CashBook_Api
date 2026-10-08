package com.cashbook.cashbook;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashbookRequestDto {

    @NotBlank(message = "Cashbook name is required")
    private String name;

    private String description;

    @NotNull(message = "Business ID is required")
    private Long businessId;
}
