package com.cashbook.business;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusinessMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Business toEntity(BusinessRequestDto request);

    @Mapping(target = "ownerId", source = "owner.id")
    BusinessResponseDTO toResponse(Business business);
}
