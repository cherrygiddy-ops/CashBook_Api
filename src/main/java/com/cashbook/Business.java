package com.cashbook;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "businesses")
@Getter
@Setter
public class Business extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessName;

    private String email;

    private String phoneNumber;

    private String address;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Cashbook_User owner;
}
