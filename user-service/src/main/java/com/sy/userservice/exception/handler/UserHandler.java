package com.sy.userservice.exception.handler;

import com.sy.userservice.common.FailureStatus;
import com.sy.userservice.exception.ApiException;

public class UserHandler extends ApiException {
    public UserHandler (FailureStatus status) {
        super(status);
    }
}
