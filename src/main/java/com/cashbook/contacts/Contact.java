package com.cashbook.contacts;
import com.cashbook.BaseEntity;
import com.cashbook.business.Business;
import com.cashbook.transactions.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contacts")
@Getter
@Setter
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phoneNumber;

    private String email;

    @ManyToOne
    private Business business;

    @OneToMany(mappedBy = "contact")
    private List<Transaction> transactions = new ArrayList<>();
}
