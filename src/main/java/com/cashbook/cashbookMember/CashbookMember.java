package com.cashbook.cashbookMember;
import com.cashbook.BaseEntity;
import com.cashbook.auth.users.User; // Adjust to your actual user entity
import com.cashbook.cashbook.Cashbook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "cashbook_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cashbook_member",
                        columnNames = {"cashbook_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_cashbook_members_cashbook", columnList = "cashbook_id"),
                @Index(name = "idx_cashbook_members_user", columnList = "user_id")
        }
)
@Getter
@Setter
public class CashbookMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cashbook_id", nullable = false)
    private Cashbook cashbook;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false, length = 20)
    private CashbookMemberRole memberRole;

}

