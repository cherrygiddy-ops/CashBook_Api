package com.cashbook.contacts;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContactResponseDto {

    private Long id;

    private String name;

    private String phoneNumber;

    private String email;

    private Long businessId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
