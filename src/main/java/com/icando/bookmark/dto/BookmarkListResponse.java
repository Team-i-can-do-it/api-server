package com.icando.bookmark.dto;

import com.icando.bookmark.entity.Bookmark;
import com.icando.referenceMaterial.entity.ReferenceMaterial;
import lombok.Data;

@Data
public class BookmarkListResponse {
    private long bookmarkId;
    private String url;
    private String imageUrl;
    public static BookmarkListResponse of(Bookmark bookmark) {
        ReferenceMaterial referenceMaterial = bookmark.getReferenceMaterial();
        BookmarkListResponse bookmarkListResponse = new BookmarkListResponse();
        bookmarkListResponse.setBookmarkId(bookmark.getId());
        bookmarkListResponse.setUrl(referenceMaterial.getUrl());
        bookmarkListResponse.setImageUrl(referenceMaterial.getImageUrl());
        return bookmarkListResponse;
    }
}
