package com.sy.userservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterRequestDto {
    String email;
    String password;
    String nickname;
}
