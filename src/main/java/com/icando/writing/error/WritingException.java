package com.icando.writing.error;

import com.icando.global.error.core.BaseException;
import com.icando.global.error.core.ErrorCode;

public class WritingException extends BaseException {
    public WritingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
