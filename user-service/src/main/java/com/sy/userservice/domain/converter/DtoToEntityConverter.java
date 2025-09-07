package com.sy.userservice.domain.converter;

import com.sy.userservice.domain.User;
import com.sy.userservice.dto.RegisterRequestDto;

import java.math.BigDecimal;

public class DtoToEntityConverter {

    public static User RegisterRequestToUserEntity(RegisterRequestDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .nickname(dto.getNickname())
                .build();
    }
}
