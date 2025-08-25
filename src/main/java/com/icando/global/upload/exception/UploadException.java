package com.icando.global.upload.exception;

import com.icando.global.error.core.BaseException;

public class UploadException extends BaseException {

    public UploadException(UploadErrorCode errorCode) {super(errorCode);}
}
