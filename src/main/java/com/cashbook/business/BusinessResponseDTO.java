package com.cashbook.business;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BusinessResponseDTO {

    private Long id;

    private String businessName;

    private String email;

    private String phoneNumber;

    private String address;

    private Long ownerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
