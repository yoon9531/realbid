package com.sy.userservice.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ReasonDto {

    private HttpStatus httpStatus;

    private Boolean isSuccess;
    private String code;
    private String message;
}
