package com.icando.bookmark.controller;

import com.icando.bookmark.dto.BookmarkAddRequest;
import com.icando.bookmark.dto.BookmarkAddResponse;
import com.icando.bookmark.enums.BookmarkSuccessCode;
import com.icando.bookmark.service.BookmarkService;
import com.icando.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<SuccessResponse<BookmarkAddResponse>> addBookmark(@RequestBody BookmarkAddRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        BookmarkAddResponse response = bookmarkService.addBookmark(request, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.of(BookmarkSuccessCode.SUCCESS_ADD_BOOKMARK, response));
    }
}
