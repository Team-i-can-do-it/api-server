package com.icando.paragraphCompletion.service;

import com.icando.global.dto.PagedResponse;
import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.paragraphCompletion.dto.ParagraphCompletionListResponse;
import com.icando.paragraphCompletion.dto.ParagraphCompletionRequest;
import com.icando.paragraphCompletion.dto.ParagraphCompletionResponse;
import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import com.icando.paragraphCompletion.entity.ParagraphWord;
import com.icando.paragraphCompletion.entity.WordSetItem;
import com.icando.paragraphCompletion.enums.ParagraphCompletionErrorCode;
import com.icando.paragraphCompletion.repository.ParagraphCompletionRepository;
import com.icando.paragraphCompletion.repository.ParagraphWordRepository;
import com.icando.paragraphCompletion.repository.WordSetItemRepository;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParagraphCompletionServiceTest {

    @Mock
    private ParagraphCompletionRepository paragraphCompletionRepository;

    @Mock
    private WordSetItemRepository wordSetItemRepository;

    @Mock
    private ParagraphWordRepository paragraphWordRepository;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ParagraphCompletionService paragraphCompletionService;

    @BeforeEach
    void setUp() {
//        when(chatClientBuilder.build()).thenReturn(chatClient);
    }

    @Test
    @DisplayName("정상적으로 요청한 개수만큼 단어를 생성한다")
    void generateWords_Success() {
        // given
        int count = 3;
        WordSetItem word1 = createWordSetItem(1, "안녕");
        WordSetItem word2 = createWordSetItem(2, "세상");
        WordSetItem word3 = createWordSetItem(3, "테스트");

        when(wordSetItemRepository.getRandomWords(anyInt()))
            .thenReturn(List.of(word1, word2, word3));

        // when
        List<String> result = paragraphCompletionService.generateWords(count);

        // then
        assertEquals(count, result.size());
        assertEquals("안녕", result.get(0));
        assertEquals("세상", result.get(1));
        assertEquals("테스트", result.get(2));
        verify(wordSetItemRepository, times(1)).getRandomWords(anyInt());
    }

    @Test
    @DisplayName("0개 요청 시 빈 리스트를 반환한다")
    void generateWords_ZeroCount() {
        // given
        int count = 0;

        // when
        List<String> result = paragraphCompletionService.generateWords(count);

        // then
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
        verify(wordSetItemRepository, times(1)).getRandomWords(anyInt());
    }

    @Test
    @DisplayName("단일 단어 생성 테스트")
    void generateWords_SingleWord() {
        // given
        int count = 1;
        WordSetItem word = createWordSetItem(1, "단어");

        when(wordSetItemRepository.getRandomWords(anyInt())).thenReturn(List.of(word));

        // when
        List<String> result = paragraphCompletionService.generateWords(count);

        // then
        assertEquals(1, result.size());
        assertEquals("단어", result.get(0));
        verify(wordSetItemRepository, times(1)).getRandomWords(anyInt());
    }

    private WordSetItem createWordSetItem(int id, String word) {
        // WordSetItem을 더 정확하게 모킹하기 위해 개별 객체로 생성
        WordSetItem wordSetItem = mock(WordSetItem.class);
//        when(wordSetItem.getId()).thenReturn(id);
        when(wordSetItem.getWord()).thenReturn(word);
        return wordSetItem;
    }

    @Test
    @DisplayName("문장 완성 글쓰기 성공")
    void insertParagraphCompletionArticle_Success() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        // given
        Member mb1 = mock(Member.class);
        when(mb1.getId()).thenReturn(1L);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(mb1));
        ParagraphCompletionRequest req = createParagraphCompletionRequest("이것은 테스트 문장입니다.", List.of("테스트", "문장", "입니다"));
        ParagraphCompletion pc = createParagraphCompletion("이것은 테스트 문장입니다.", List.of("테스트", "문장", "입니다"));

        when(paragraphCompletionRepository.save(any())).thenReturn(pc);

        // when & then
        var res = paragraphCompletionService.insertParagraphCompletionArticle(mb1.getId(), req);

        assertEquals(req.getContent(), res.getContent());
        assertEquals(3, res.getWords().size());

        verify(memberRepository, times(1)).findById(anyLong());
        verify(paragraphCompletionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("단어가 문장에 포함되지 않은 경우 예외 발생")
    void insertParagraphCompletionArticle_WordNotInContent() {
        // given
        Member mb1 = mock(Member.class);
        when(mb1.getId()).thenReturn(1L);
        ParagraphCompletionRequest req = createParagraphCompletionRequest("이것은 테스트 문장입니다.", List.of("테스트", "없는단어"));
        // when & then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            paragraphCompletionService.insertParagraphCompletionArticle(mb1.getId(), req);
        });
        assertEquals(ParagraphCompletionErrorCode.WORD_NOT_IN_CONTENT.getMessage(), exception.getMessage());
        verify(memberRepository, times(0)).findById(anyLong());
        verify(paragraphCompletionRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 예외 발생")
    void insertParagraphCompletionArticle_UserNotFound() {
        // given
        Long nonExistentUserId = 999L;
        when(memberRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        ParagraphCompletionRequest req = createParagraphCompletionRequest("이것은 테스트 문장입니다.", List.of("테스트", "문장"));
        // when & then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            paragraphCompletionService.insertParagraphCompletionArticle(nonExistentUserId, req);
        });
        assertEquals(ParagraphCompletionErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
        verify(memberRepository, times(1)).findById(nonExistentUserId);
        verify(paragraphCompletionRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("빈 단어 리스트로 문장 완성 글쓰기 시도")
    void insertParagraphCompletionArticle_EmptyContentAndWords() {
        // given
        ParagraphCompletionRequest req = createParagraphCompletionRequest("", List.of());
        // when & then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            paragraphCompletionService.insertParagraphCompletionArticle(1L, req);
        });
        assertEquals(ParagraphCompletionErrorCode.INVALID_WORD_COUNT.getMessage(), exception.getMessage());
        verify(memberRepository, times(0)).findById(anyLong());
        verify(paragraphCompletionRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("빈 문장으로 문장 완성 글쓰기 시도")
    void insertParagraphCompletionArticle_EmptyContent() {
        // given
        ParagraphCompletionRequest req = createParagraphCompletionRequest("", List.of("테스트", "문장"));
        // when & then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            paragraphCompletionService.insertParagraphCompletionArticle(1L, req);
        });
        assertEquals(ParagraphCompletionErrorCode.INVALID_CONTENT.getMessage(), exception.getMessage());
        verify(memberRepository, times(0)).findById(anyLong());
        verify(paragraphCompletionRepository, times(0)).save(any());
    }

    private ParagraphCompletionRequest createParagraphCompletionRequest(String content, List<String> words) {
        ParagraphCompletionRequest request = mock(ParagraphCompletionRequest.class);
        if (content != null && !content.isEmpty()) {
            when(request.getContent()).thenReturn(content);
        }
        if (words != null && !words.isEmpty()) {
            when(request.getWords()).thenReturn(words);
        }
        return request;
    }

    private ParagraphCompletion createParagraphCompletion(String content, List<String> words) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        Constructor<ParagraphCompletion> constructor = ParagraphCompletion.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ParagraphCompletion pc = constructor.newInstance();

        FieldUtils.writeField(pc, "content", content, true);

        var pwList = words.stream().map(word -> {
            var pw = mock(ParagraphWord.class);
            try {
                FieldUtils.writeField(pw, "word", word, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return pw;
        }).toList();

        FieldUtils.writeField(pc, "paragraphWords", pwList, true);
        return pc;
    }

    @Test
    @DisplayName("문단완성 글 조회 성공")
    void getParagraphCompletion_Success() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        //given
        Member member = mock(Member.class);
        ParagraphCompletion paragraphCompletion = createParagraphCompletion("테스트 문장입니다.", List.of("테스트", "문장", "입니다"));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(paragraphCompletionRepository.findByIdAndMember(anyLong(), eq(member))).thenReturn(Optional.of(paragraphCompletion));

        // when & then
        ParagraphCompletionResponse response = paragraphCompletionService.getParagraphCompletionArticle(1L, 1L);

        assertEquals("테스트 문장입니다.", response.getContent());
        assertEquals(3, response.getWords().size());

        verify(paragraphCompletionRepository, times(0)).save(any());
        verify(memberRepository, times(1)).findById(anyLong());
        verify(paragraphCompletionRepository, times(1)).findByIdAndMember(anyLong(), any());
    }

    @Test
    @DisplayName("없는 사용자로 문단완성 글 조회 시도")
    void getParagraphCompletion_UserNotFound() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when & then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            paragraphCompletionService.getParagraphCompletionArticle(999L, 1L);
        });
        assertEquals(exception.getMessage(), ParagraphCompletionErrorCode.USER_NOT_FOUND.getMessage());

        verify(memberRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("자신의 글이 아닌 / 존재하지 않는 글 조회 시도")
    void getParagraphCompletion_PostNotFound() {
        // given
        Member member = mock(Member.class);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(paragraphCompletionRepository.findByIdAndMember(anyLong(), any())).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            paragraphCompletionService.getParagraphCompletionArticle(1L, 1L);
        });

        assertEquals(exception.getMessage(), ParagraphCompletionErrorCode.PARAGRAPH_COMPLETION_NOT_FOUND.getMessage());

        verify(memberRepository, times(1)).findById(anyLong());
        verify(paragraphCompletionRepository, times(1)).findByIdAndMember(anyLong(), any());
    }


    @Test
    @DisplayName("문단완성 글 목록 조회 성공")
    void getAllParagraphCompletion_Success() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        //given
        Member member = mock(Member.class);
        ParagraphCompletion paragraphCompletion = createParagraphCompletion("테스트 문장입니다.", List.of("테스트", "문장", "입니다"));

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        Page<ParagraphCompletion> paragraphCompletions = mock(Page.class);
        when(paragraphCompletionRepository.findAllByMember(any(), any())).thenReturn(paragraphCompletions);
        when(paragraphCompletions.getTotalElements()).thenReturn(1L);
        when(paragraphCompletions.getTotalPages()).thenReturn(123);
        when(paragraphCompletions.stream()).thenReturn(Stream.of(
                paragraphCompletion
        ));

        when(paragraphCompletionRepository.findAllByMember(any(), any())).thenReturn(paragraphCompletions);
        // when & then
        PagedResponse<ParagraphCompletionListResponse> result = paragraphCompletionService.getAllParagraphCompletionArticle(1L, 20, 1);

        assertEquals(1, result.getTotalElements());
        assertEquals(123, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        verify(paragraphCompletionRepository, times(0)).save(any());

        verify(memberRepository, times(1)).findById(anyLong());
        verify(paragraphCompletionRepository, times(1)).findAllByMember(any(), any());
    }

    @Test
    @DisplayName("없는 사용자로 문단완성 글 목록 조회 시도")
    void getAllParagraphCompletion_UserNotFound() {
        // given
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when & then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            paragraphCompletionService.getAllParagraphCompletionArticle(999L, 20, 1);
        });
        assertEquals(exception.getMessage(), ParagraphCompletionErrorCode.USER_NOT_FOUND.getMessage());

        verify(memberRepository, times(1)).findById(anyLong());
    }
}