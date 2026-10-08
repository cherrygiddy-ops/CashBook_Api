package com.cashbook.category;
import com.cashbook.BaseEntity;
import com.cashbook.business.Business;
import com.cashbook.transactions.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Business business;

    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions = new ArrayList<>();
}
