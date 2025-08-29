package com.icando.member.login.exception;

import com.icando.global.error.core.BaseException;

public class AuthException extends BaseException {
    public AuthException(AuthErrorCode message) {
        super(message);
    }
}
