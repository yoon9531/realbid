package com.sy.userservice.exception.handler;

import com.sy.userservice.common.ApiResponse;
import com.sy.userservice.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiResponse<?>> handleApiException(ApiException e) {
        return ResponseEntity
                .status(e.getReason().getHttpStatus())
                .body(ApiResponse.failure(e.getReason().getCode(), e.getReason().getMessage(), null));
    }
}
