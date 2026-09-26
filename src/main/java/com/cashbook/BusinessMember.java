package com.cashbook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "business_members")
@Getter
@Setter
public class BusinessMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Business business;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;
}
