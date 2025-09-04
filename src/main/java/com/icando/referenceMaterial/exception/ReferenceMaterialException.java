package com.icando.referenceMaterial.exception;

import com.icando.global.error.core.BaseException;
import com.icando.global.error.core.ErrorCode;

public class ReferenceMaterialException extends BaseException {
    public ReferenceMaterialException(ErrorCode errorCode) {
        super(errorCode);
    }
}

