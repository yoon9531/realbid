package com.sy.authservice.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    // ===== Common (COM_XXX) =====
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COM_001", "Invalid input value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COM_002", "Method not allowed"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COM_003", "Entity not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COM_004", "Server error"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COM_005", "Invalid type value"),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "COM_006", "Access is denied"),

    // ===== Auth (AUTH_XXX) =====
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "AUTH_001", "Email is duplicated"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH_002", "Token is invalid"),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "Token is expired"),
    AUTH_TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "AUTH_004", "Token is empty"),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "AUTH_005", "Unsupported token");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
