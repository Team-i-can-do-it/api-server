package com.icando.paragraphCompletion.exception;

import com.icando.global.error.core.BaseException;
import com.icando.global.error.core.ErrorCode;

public class ParagraphCompletionException extends BaseException {
    public ParagraphCompletionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
