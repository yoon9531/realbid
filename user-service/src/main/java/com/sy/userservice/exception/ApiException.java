package com.sy.userservice.exception;

import com.sy.userservice.common.ApiStatus;
import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.common.ReasonDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ApiException extends RuntimeException{
    private FailureStatus status;

    public ApiException(FailureStatus status) {
        this.status = status;
    }

    public ReasonDto getReason() {
        return status.getReason();
    }

    public HttpStatus getReasonHttpStatus() {
        return this.status.getHttpStatus();
    }
}
