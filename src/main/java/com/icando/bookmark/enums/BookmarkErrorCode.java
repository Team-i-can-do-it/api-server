package com.icando.bookmark.enums;

import com.icando.global.error.core.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum BookmarkErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    REFRENCE_MATERIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "참고 자료를 찾을 수 없습니다."),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 북마크에 추가된 자료입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
