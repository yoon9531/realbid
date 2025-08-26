package com.sy.userservice.common;

import org.springframework.http.HttpStatus;

public interface ApiStatus {

    ReasonDto getReason();

    HttpStatus getHttpStatus();
}
