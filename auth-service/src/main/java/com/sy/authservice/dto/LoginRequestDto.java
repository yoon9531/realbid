package com.sy.authservice.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}