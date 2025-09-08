package com.icando.bookmark.exception;

import com.icando.global.error.core.BaseException;
import com.icando.global.error.core.ErrorCode;

public class BookmarkException extends BaseException {

    public BookmarkException(ErrorCode errorCode) {
        super(errorCode);
    }
}
