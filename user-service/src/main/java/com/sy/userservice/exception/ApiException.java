package com.sy.userservice.exception;

import com.sy.userservice.common.ApiStatus;
import com.sy.userservice.common.ReasonDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ApiException extends RuntimeException{
    private ApiStatus apiStatus;

    public ApiException(ApiStatus apiStatus) {
        this.apiStatus = apiStatus;
    }

    public ReasonDto getReason() {
        return apiStatus.getReason();
    }

    public HttpStatus getReasonHttpStatus() {
        return this.apiStatus.getHttpStatus();
    }
}
