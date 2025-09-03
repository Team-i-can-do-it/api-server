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
import com.icando.paragraphCompletion.exception.ParagraphCompletionErrorCode;
import com.icando.paragraphCompletion.exception.ParagraphCompletionException;
import com.icando.paragraphCompletion.repository.ParagraphCompletionRepository;
import com.icando.paragraphCompletion.repository.ParagraphWordRepository;
import com.icando.paragraphCompletion.repository.WordSetItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ParagraphCompletionService {
    private final ParagraphCompletionRepository paragraphCompletionRepository;
    private final WordSetItemRepository wordSetItemRepository;
    private final MemberRepository memberRepository;
    private final ParagraphWordRepository paragraphWordRepository;


    public ParagraphCompletionService(ParagraphCompletionRepository paragraphCompletionRepository, WordSetItemRepository wordSetItemRepository, MemberRepository memberRepository, ParagraphWordRepository paragraphWordRepository) {
        this.paragraphCompletionRepository = paragraphCompletionRepository;
        this.wordSetItemRepository = wordSetItemRepository;
        this.memberRepository = memberRepository;
        this.paragraphWordRepository = paragraphWordRepository;
    }

    public List<String> generateWords(int count) {
        return wordSetItemRepository.getRandomWords(count).stream().map(WordSetItem::getWord).toList();
    }

    @Transactional
    public ParagraphCompletionResponse insertParagraphCompletionArticle(String memberEmail, ParagraphCompletionRequest paragraphCompletionRequest) {
        if (paragraphCompletionRequest.getWords() == null || paragraphCompletionRequest.getWords().isEmpty()) {
            throw new ParagraphCompletionException(ParagraphCompletionErrorCode.INVALID_WORD_COUNT);
        }

        if (paragraphCompletionRequest.getContent() == null || paragraphCompletionRequest.getContent().isEmpty()) {
            throw new ParagraphCompletionException(ParagraphCompletionErrorCode.INVALID_CONTENT);
        }

        paragraphCompletionRequest.getWords().forEach(word -> {
            if (!paragraphCompletionRequest.getContent().contains(word)) {
                throw new ParagraphCompletionException(ParagraphCompletionErrorCode.WORD_NOT_IN_CONTENT);
            }
        });

        Optional<Member> member = memberRepository.findByEmail(memberEmail);
        if (member.isEmpty()) {
            throw new ParagraphCompletionException(ParagraphCompletionErrorCode.USER_NOT_FOUND);
        }

        ParagraphCompletion paragraphCompletion = ParagraphCompletion.of(
                paragraphCompletionRequest.getContent(),
                member.get()
        );

        List<ParagraphWord> paragraphWords = paragraphCompletionRequest.getWords().stream()
                .map(word -> ParagraphWord.of(word, paragraphCompletion))
                .toList();
        paragraphCompletion.getParagraphWords().addAll(paragraphWords);

        ParagraphCompletion savedParagraphCompletion = paragraphCompletionRepository.save(paragraphCompletion);

        paragraphWordRepository.saveAll(paragraphWords);

        return ParagraphCompletionResponse.of(savedParagraphCompletion);
    }

    public ParagraphCompletionResponse getParagraphCompletionArticle(String memberEmail, Long id) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new ParagraphCompletionException(ParagraphCompletionErrorCode.USER_NOT_FOUND));

        ParagraphCompletion paragraphCompletion = paragraphCompletionRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new ParagraphCompletionException(ParagraphCompletionErrorCode.PARAGRAPH_COMPLETION_NOT_FOUND));

        return ParagraphCompletionResponse.of(paragraphCompletion);
    }

    public PagedResponse<ParagraphCompletionListResponse> getAllParagraphCompletionArticle(String memberEmail, int pageSize, int page) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new ParagraphCompletionException(ParagraphCompletionErrorCode.USER_NOT_FOUND));

        Page<ParagraphCompletion> paragraphCompletions =
                paragraphCompletionRepository.findAllByMember(member, PageRequest.of(page - 1, pageSize));

        long totalElements = paragraphCompletions.getTotalElements();
        int totalPages = paragraphCompletions.getTotalPages();
        return PagedResponse.of(paragraphCompletions.stream()
                .map(ParagraphCompletionListResponse::of)
                .toList(), page, pageSize, totalElements, totalPages);
    }
}
