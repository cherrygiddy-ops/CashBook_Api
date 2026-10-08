package com.cashbook.category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "business.id", target = "businessId")
    CategoryResponseDto toResponse(Category category);
}