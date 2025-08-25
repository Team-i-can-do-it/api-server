package com.icando.writing.error;

import com.icando.global.error.core.BaseException;
import com.icando.global.error.core.ErrorCode;

public class TopicException extends BaseException {

    public TopicException(ErrorCode errorCode) {
        super(errorCode);
    }
}
