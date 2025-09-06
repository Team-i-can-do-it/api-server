package com.icando.bookmark.service;

import com.icando.bookmark.dto.BookmarkAddRequest;
import com.icando.bookmark.dto.BookmarkAddResponse;
import com.icando.bookmark.entity.Bookmark;
import com.icando.bookmark.repository.BookmarkRepository;
import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.referenceMaterial.entity.ReferenceMaterial;
import com.icando.referenceMaterial.repository.ReferenceMaterialRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {
    @InjectMocks
    BookmarkService bookmarkService;

    @Mock
    BookmarkRepository bookmarkRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ReferenceMaterialRepository referenceMaterialRepository;

    @Test
    @DisplayName("북마크 추가 성공")
    void addBookmark_Success() {
        // Given
        long bookmarkId = 1L;
        long referenceMaterialId = 1L;
        String email = "test@test.com";
        Bookmark bookmark = mock(Bookmark.class);
        when(bookmark.getId()).thenReturn(bookmarkId);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(Member.class)));
        when(referenceMaterialRepository.findById(anyLong())).thenReturn(Optional.of(mock(ReferenceMaterial.class)));
        when(bookmarkRepository.existsByMemberAndReferenceMaterial(any(), any())).thenReturn(false);
        when(bookmarkRepository.save(any())).thenReturn(bookmark);

        // When & Then
        BookmarkAddResponse response = bookmarkService.addBookmark(
                new BookmarkAddRequest(referenceMaterialId), email);

        assertEquals(bookmarkId, response.getBookmarkId());

        verify(bookmarkRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("북마크 목록 조회 성공")
    void getBookmarks_Success() {
        // Given
        String email = "test@test.com";
        PageRequest pageable = PageRequest.of(0, 10);

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(Member.class)));
        when(bookmarkRepository.findAllByMember(any(), any())).thenReturn(new PageImpl<>(
                List.of(mock(Bookmark.class), mock(Bookmark.class))
        ));
        // When & Then

        Page<Bookmark> bookmarks = bookmarkRepository.findAllByMember(
                memberRepository.findByEmail(email).orElseThrow(), pageable);
        assertEquals(2, bookmarks.getContent().size());
        assertEquals(2, bookmarks.getTotalElements());

        verify(bookmarkRepository, times(1)).findAllByMember(any(), any());
    }

    @Test
    @DisplayName("북마크 삭제 성공")
    void deleteBookmark_Success() {
        // Given
        long bookmarkId = 1L;
        String email = "test@test.com";

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(Member.class)));
        when(bookmarkRepository.findBookmarkByIdAndMember(anyLong(), any())).thenReturn(Optional.of(mock(Bookmark.class)));
        doNothing().when(bookmarkRepository).delete(any());

        // When & Then
        assertDoesNotThrow(() -> bookmarkService.deleteBookmark(bookmarkId, email));
        verify(bookmarkRepository, times(1)).delete(any());
    }
}