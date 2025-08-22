package com.icando.community.post.exception;

import com.icando.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PostSuccessCode implements SuccessCode {
    SUCCESS_CREATE_POST(HttpStatus.CREATED,"게시글이 작성 되었습니다");

    private final HttpStatus status;
    private final String message;

    PostSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

