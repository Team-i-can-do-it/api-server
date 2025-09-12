package com.icando.paragraphCompletion.service;

import com.icando.feedback.dto.FeedbackRequest;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.entity.Feedback;
import com.icando.feedback.service.FeedbackService;
import com.icando.global.utils.GlobalLogger;
import com.icando.member.entity.ActivityType;
import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.paragraphCompletion.dto.ParagraphCompletionListResponse;
import com.icando.paragraphCompletion.dto.ParagraphCompletionRequest;
import com.icando.paragraphCompletion.dto.ParagraphCompletionResponse;
import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import com.icando.paragraphCompletion.entity.ParagraphWord;
import com.icando.paragraphCompletion.entity.WordSetItem;
import com.icando.paragraphCompletion.enums.ParagraphCompletionErrorCode;
import com.icando.paragraphCompletion.exception.ParagraphCompletionException;
import com.icando.paragraphCompletion.repository.ParagraphCompletionRepository;
import com.icando.paragraphCompletion.repository.ParagraphWordRepository;
import com.icando.paragraphCompletion.repository.WordSetItemRepository;
import com.icando.writing.enums.WritingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class ParagraphCompletionService {
    private final ParagraphCompletionRepository paragraphCompletionRepository;
    private final WordSetItemRepository wordSetItemRepository;
    private final MemberRepository memberRepository;
    private final ParagraphWordRepository paragraphWordRepository;
    private final List<String> wordList = new ArrayList<>();
    private final FeedbackService feedbackService;


    public ParagraphCompletionService(ParagraphCompletionRepository paragraphCompletionRepository, WordSetItemRepository wordSetItemRepository, MemberRepository memberRepository, ParagraphWordRepository paragraphWordRepository, FeedbackService feedbackService) {
        this.paragraphCompletionRepository = paragraphCompletionRepository;
        this.wordSetItemRepository = wordSetItemRepository;
        this.memberRepository = memberRepository;
        this.paragraphWordRepository = paragraphWordRepository;
        this.feedbackService = feedbackService;
    }

//    public List<String> generateWords(int count) {
//        return wordSetItemRepository.getRandomWords(count).stream().map(WordSetItem::getWord).toList();
//    }

    public List<String> generateWords(int count) {
        List<String> words = new ArrayList<>(count);
        HashSet<Integer> indexes = new HashSet<>();
        Random random = new Random();

        if (wordList.isEmpty()) {
            List<String> allWords = wordSetItemRepository.findAll().stream()
                    .map(WordSetItem::getWord)
                    .toList();
            wordList.addAll(allWords);
        }

        while (indexes.size() < count) {
            int randomIndex = random.nextInt(wordList.size());
            indexes.add(randomIndex);
        }

        for (int index : indexes) {
            words.add(wordList.get(index));
        }

        return words;
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

        feedbackService.generateFeedback(new FeedbackRequest(WritingType.PARAGRAPH_COMPLETION, savedParagraphCompletion.getId()), ActivityType.WORD);

        savedParagraphCompletion = paragraphCompletionRepository.findById(savedParagraphCompletion.getId()).orElseThrow(() -> new ParagraphCompletionException(ParagraphCompletionErrorCode.PARAGRAPH_COMPLETION_NOT_FOUND));

        return ParagraphCompletionResponse.of(savedParagraphCompletion);
    }

    public ParagraphCompletionResponse getParagraphCompletionArticle(String memberEmail, Long id) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new ParagraphCompletionException(ParagraphCompletionErrorCode.USER_NOT_FOUND));

        ParagraphCompletion paragraphCompletion = paragraphCompletionRepository.findByIdAndMember(id, member)
                .orElseThrow(() -> new ParagraphCompletionException(ParagraphCompletionErrorCode.PARAGRAPH_COMPLETION_NOT_FOUND));

        return ParagraphCompletionResponse.of(paragraphCompletion);
    }

    public Page<ParagraphCompletionListResponse> getAllParagraphCompletionArticle(String memberEmail, int pageSize, int page, String sortBy, boolean isAsc) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new ParagraphCompletionException(ParagraphCompletionErrorCode.USER_NOT_FOUND));

        Page<ParagraphCompletion> paragraphCompletions =
                paragraphCompletionRepository.findAllByMember(member, PageRequest.of(page - 1, pageSize, isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()));

        return paragraphCompletions
                .map(ParagraphCompletionListResponse::of);
    }
}
