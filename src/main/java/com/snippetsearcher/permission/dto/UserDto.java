package com.snippetsearcher.permission.dto;

import com.snippetsearcher.permission.model.UserAccount;
import java.util.UUID;

public record UserDto(
        UUID id,
        String name,
        String email
) {
    public static UserDto from(UserAccount acc) {
        return new UserDto(
                acc.getId(),
                acc.getName(),
                acc.getEmail()
        );
    }
}

