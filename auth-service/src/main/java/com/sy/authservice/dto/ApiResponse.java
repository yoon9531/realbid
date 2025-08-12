package com.sy.authservice.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonPropertyOrder({"success", "data", "message"})
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;


    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null);
    }

    public static <T>  ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T>  ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static <T> ApiResponse<T> of() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<?> failure(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
