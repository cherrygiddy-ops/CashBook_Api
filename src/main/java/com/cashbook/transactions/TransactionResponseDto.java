package com.cashbook.transactions;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponseDto {

    private Long id;

    private String transactionNumber;

    private LocalDate transactionDate;

    private TransactionType type;

    private BigDecimal amount;

    private BigDecimal runningBalance;

    private String remarks;

    private PaymentMode paymentMode;

    private Long cashbookId;

    private Long categoryId;

    private Long contactId;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}