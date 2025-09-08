package com.icando.bookmark.controller;

import com.icando.bookmark.dto.BookmarkAddRequest;
import com.icando.bookmark.dto.BookmarkAddResponse;
import com.icando.bookmark.dto.BookmarkListResponse;
import com.icando.bookmark.enums.BookmarkSuccessCode;
import com.icando.bookmark.service.BookmarkService;
import com.icando.global.success.SuccessResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<SuccessResponse<BookmarkAddResponse>> addBookmark(@RequestBody BookmarkAddRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        BookmarkAddResponse response = bookmarkService.addBookmark(request, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.of(BookmarkSuccessCode.SUCCESS_ADD_BOOKMARK, response));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<BookmarkListResponse>>> getBookmarks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @Valid @Min(0) int page,
            @RequestParam @Valid @Min(1) @Max(100) int pageSize) {
        Page<BookmarkListResponse> response = bookmarkService.getBookmarks(userDetails.getUsername(), page, pageSize);
        return ResponseEntity.ok(SuccessResponse.of(BookmarkSuccessCode.SUCCESS_GET_LIST_BOOKMARKS, response));
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<Void>> deleteBookmark(@RequestParam Long bookmarkId, @AuthenticationPrincipal UserDetails userDetails) {
        bookmarkService.deleteBookmark(bookmarkId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.of(BookmarkSuccessCode.SUCCESS_DELETE_BOOKMARK));
    }
}
