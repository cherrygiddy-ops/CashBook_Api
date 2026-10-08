package com.cashbook.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {

    @NotBlank(message = "Category name is required")
    private String name;

    @NotNull(message = "Business ID is required")
    private Long businessId;
}