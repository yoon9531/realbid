package com.sy.userservice.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    private Boolean isSuccess;
    private String code;
    private String message;
    private T result;

    // 성공했을 경우
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

    // 실패했을 경우
    public static <T> ApiResponse<T> failure(String code, String message, T result) {
        return new ApiResponse<>(false, code, message, result);
    }
}
