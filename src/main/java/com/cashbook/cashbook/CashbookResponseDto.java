package com.cashbook.cashbook;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CashbookResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long businessId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
