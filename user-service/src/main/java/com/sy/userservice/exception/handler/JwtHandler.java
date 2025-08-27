package com.sy.userservice.exception.handler;

import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.exception.ApiException;

public class JwtHandler extends ApiException {
    public JwtHandler (FailureStatus status) {
        super(status);
    }
}
