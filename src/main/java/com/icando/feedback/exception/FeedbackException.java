package com.icando.feedback.exception;

import com.icando.global.error.core.BaseException;
import com.icando.global.error.core.ErrorCode;

public class FeedbackException extends BaseException {

    public FeedbackException(ErrorCode errorCode) {
        super(errorCode);
    }
}
