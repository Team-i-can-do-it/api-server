package com.icando.member.exception;

import com.icando.global.error.core.BaseException;

public class MemberException extends BaseException {

    public MemberException(MemberErrorCode errorCode) {super(errorCode);}
}


