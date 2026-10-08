package com.cashbook.businessMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusinessMemberMapper {

    @Mapping(source = "business.id", target = "businessId")
    @Mapping(source = "cashbookuser.id", target = "userId")
    BusinessMemberResponseDto toResponse(BusinessMember member);
}
