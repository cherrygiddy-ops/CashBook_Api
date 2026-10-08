package com.cashbook.businessMember;


import com.cashbook.auth.users.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessMemberRequestDto {

    @NotNull
    private Long businessId;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private Member_Role memberRole;
}
