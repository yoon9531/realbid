package com.sy.authservice.exception.handler;

import com.sy.authservice.exception.GeneralException;
import com.sy.authservice.exception.code.ErrorCode;

public class JwtHandler extends GeneralException {

    public JwtHandler(ErrorCode errorCode) {
        super(errorCode);
    }
}
