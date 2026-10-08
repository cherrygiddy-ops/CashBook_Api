package com.cashbook.businessMember;

import com.cashbook.auth.users.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BusinessMemberResponseDto {

    private Long id;

    private Long businessId;

    private Long userId;

    private Member_Role memberRole;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
