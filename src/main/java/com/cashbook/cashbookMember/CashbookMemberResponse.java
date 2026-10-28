package com.cashbook.cashbookMember;

import java.time.LocalDateTime;

public record CashbookMemberResponse(
        Long id,
        Long userId,
        String username,
        String phoneNumber,
        CashbookMemberRole memberRole,
        LocalDateTime createdAt
) {
}
