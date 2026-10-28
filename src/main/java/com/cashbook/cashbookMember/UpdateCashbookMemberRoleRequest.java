package com.cashbook.cashbookMember;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCashbookMemberRoleRequest {

    @NotNull(message = "Member role is required")
    private CashbookMemberRole memberRole;
}
