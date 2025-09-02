package com.icando.global.mail.exception;

import com.icando.global.error.core.BaseException;


public class MailException extends BaseException {
    public MailException(MailErrorCode message) {
        super(message);
    }
}
