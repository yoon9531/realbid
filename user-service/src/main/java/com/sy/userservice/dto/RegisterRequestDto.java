package com.sy.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterRequestDto {
    @Email
    String email;

    @NotBlank @Size(min = 8, max = 64)
    String password;
    @NotBlank @Size(min = 2, max = 20)
    String nickname;
}
