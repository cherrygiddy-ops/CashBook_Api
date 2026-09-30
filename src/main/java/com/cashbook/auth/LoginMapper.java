package com.cashbook.auth;


import com.cashbook.auth.users.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginMapper {
    CurrentUserResponseDto toDto (User user);
}
