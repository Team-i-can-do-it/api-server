package com.icando.bookmark.service;

import com.icando.bookmark.dto.BookmarkAddRequest;
import com.icando.bookmark.dto.BookmarkAddResponse;
import com.icando.bookmark.dto.BookmarkListResponse;
import com.icando.bookmark.entity.Bookmark;
import com.icando.bookmark.enums.BookmarkErrorCode;
import com.icando.bookmark.exception.BookmarkException;
import com.icando.bookmark.repository.BookmarkRepository;
import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.referenceMaterial.entity.ReferenceMaterial;
import com.icando.referenceMaterial.repository.ReferenceMaterialRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final ReferenceMaterialRepository referenceMaterialRepository;

    public BookmarkAddResponse addBookmark(BookmarkAddRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BookmarkException(BookmarkErrorCode.USER_NOT_FOUND));
        ReferenceMaterial referenceMaterial = referenceMaterialRepository.findById(request.getReferenceMaterialId())
                .orElseThrow(() -> new BookmarkException(BookmarkErrorCode.REFRENCE_MATERIAL_NOT_FOUND));

        boolean exists = bookmarkRepository.existsByMemberAndReferenceMaterial(member, referenceMaterial);
        if (exists) {
            throw new BookmarkException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        Bookmark bookmark = bookmarkRepository.save(Bookmark.of(member, referenceMaterial));
        return new BookmarkAddResponse(bookmark.getId());
    }

    public Page<BookmarkListResponse> getBookmarks(String email, int page, int pageSize) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BookmarkException(BookmarkErrorCode.USER_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Bookmark> bookmarks = bookmarkRepository.findAllByMember(member, pageable);

        return bookmarks.map(BookmarkListResponse::of);
    }
}
