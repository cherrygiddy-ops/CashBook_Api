package com.cashbook.business;

import com.cashbook.BaseEntity;
import com.cashbook.auth.users.User;
import com.cashbook.businessMember.BusinessMember;
import com.cashbook.category.Category;
import com.cashbook.contacts.Contact;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "business")
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "business")
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "business")
    private List<BusinessMember> members = new ArrayList<>();
}