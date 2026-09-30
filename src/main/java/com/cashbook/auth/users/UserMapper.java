package com.cashbook.auth.users;


import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegistrationDto requestDto);

    UserResponseDto toDto(User userEntity);
}
