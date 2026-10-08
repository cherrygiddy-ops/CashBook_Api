package com.cashbook.cashbook;

import com.cashbook.BaseEntity;
import com.cashbook.business.Business;
import com.cashbook.transactions.Transaction;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cashbooks")
@Getter
@Setter
public class Cashbook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    private Business business;

    private BigDecimal openingBalance;

    @OneToMany(mappedBy = "cashbook")
    private List<Transaction> transactions = new ArrayList<>();
}