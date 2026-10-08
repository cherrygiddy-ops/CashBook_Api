package com.cashbook.transactions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "cashbook.id", target = "cashbookId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "contact.id", target = "contactId")
    @Mapping(source = "createdBy.first_name", target = "createdBy")
    TransactionResponseDto toResponse(Transaction transaction);
}
