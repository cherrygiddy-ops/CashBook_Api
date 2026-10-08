package com.cashbook.cashbook;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CashbookMapper {

    @Mapping(source = "business.id", target = "businessId")
    CashbookResponseDto toResponse(Cashbook cashbook);
}
