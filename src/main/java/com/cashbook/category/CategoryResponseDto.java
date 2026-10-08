package com.cashbook.category;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CategoryResponseDto {

    private Long id;
    private String name;
    private Long businessId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
