package com.icando.bookmark.enums;

import com.icando.global.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum BookmarkSuccessCode implements SuccessCode {
    SUCCESS_ADD_BOOKMARK(HttpStatus.CREATED, "북마크가 성공적으로 추가되었습니다."),
    SUCCESS_DELETE_BOOKMARK(HttpStatus.OK, "북마크가 성공적으로 삭제되었습니다."),
    SUCCESS_GET_LIST_BOOKMARKS(HttpStatus.OK, "북마크 목록을 성공적으로 조회했습니다.");

    private final HttpStatus status;
    private final String message;

}
