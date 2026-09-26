package com.cashbook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String transactionNumber;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private Double amount;

    private Double runningBalance;

    private String remarks;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @ManyToOne
    private Cashbook cashbook;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Contact contact;

    @ManyToOne
    private User createdBy;
}
