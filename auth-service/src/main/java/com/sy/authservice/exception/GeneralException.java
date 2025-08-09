package com.sy.authservice.exception;

import com.sy.authservice.exception.code.ErrorCode;
import org.springframework.http.HttpStatus;

public class GeneralException extends RuntimeException {

    private ErrorCode errorCode;

    public GeneralException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getErrorHttpStatus() {
        return errorCode.getStatus();
    }

}
