package com.icando.community.post.exception;

import com.icando.global.error.core.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PostErrorCode implements ErrorCode {

    INVALID_POST_ID(HttpStatus.BAD_REQUEST,"해당한 게시글이 존재하지 않습니다." );


    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return "[POST ERROR]" + message;
    }
}