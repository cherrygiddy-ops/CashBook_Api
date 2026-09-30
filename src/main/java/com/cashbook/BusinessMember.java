package com.cashbook;
import com.cashbook.auth.users.Role;
import com.cashbook.auth.users.User;
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
    @JoinColumn(name = "business_id")
    private Business business;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User cashbookuser;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role")
    private Role memberRole;
}
