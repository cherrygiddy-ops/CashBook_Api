package com.cashbook.contacts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(source = "business.id", target = "businessId")
    ContactResponseDto toResponse(Contact contact);
}