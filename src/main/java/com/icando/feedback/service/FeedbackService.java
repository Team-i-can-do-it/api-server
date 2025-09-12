package com.icando.feedback.service;

import com.icando.feedback.dto.FeedbackRequest;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.entity.Feedback;
import com.icando.feedback.entity.FeedbackScore;
import com.icando.feedback.exception.FeedbackErrorCode;
import com.icando.feedback.exception.FeedbackException;
import com.icando.feedback.repository.FeedbackRepository;
import com.icando.feedback.repository.FeedbackScoreRepository;
import com.icando.member.entity.ActivityType;
import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.member.service.PointService;
import com.icando.member.repository.MbtiRepository;
import com.icando.paragraphCompletion.entity.ParagraphCompletion;
import com.icando.paragraphCompletion.entity.ParagraphWord;
import com.icando.paragraphCompletion.repository.ParagraphCompletionRepository;
import com.icando.writing.entity.Topic;
import com.icando.writing.entity.Writing;
import com.icando.writing.enums.WritingType;
import com.icando.writing.repository.WritingRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final ChatClient.Builder chatClientBuilder;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackScoreRepository feedbackScoreRepository;
    private final MbtiRepository mbtiRepository;
    private final PointService pointService;
    private final WritingRepository writingRepository;
    private final ParagraphCompletionRepository paragraphCompletionRepository;
    private final MemberRepository memberRepository;

    @Value("${feedback.evaluation.prompt.feedback}")
    private String evaluationPromptFeedback;

    // TODO: 추후 stream, Flux 비동기로 성능개선
    @Transactional
    public FeedbackResponse generateFeedback(FeedbackRequest request, ActivityType activityType) {
        if (request.writingType() == WritingType.WRITING) {
            return generateWritingFeedback(request, activityType);
        } else {
            return generateParagraphCompletionFeedback(request, activityType);
        }
    }

    private FeedbackResponse generateParagraphCompletionFeedback(FeedbackRequest request, ActivityType activityType) {
        ParagraphCompletion paragraphCompletion = paragraphCompletionRepository.findById(request.writingId())
                .orElseThrow(() -> new FeedbackException(FeedbackErrorCode.WRITING_NOT_FOUND));
        String topic = paragraphCompletion.getParagraphWords().stream().map(ParagraphWord::getWord).collect(Collectors.joining(", "));

        // AI에 요청후 JSON 응답을 DTO로 변환
        FeedbackResponse aiResponse = chatClientBuilder.build()
                .prompt("다음은 유저가 선택한 단어 목록입니다. " + topic)
                .system(evaluationPromptFeedback)
                .user(paragraphCompletion.getContent())
                .call()
                .entity(FeedbackResponse.class);

        if (aiResponse == null) {
            throw new FeedbackException(FeedbackErrorCode.FEEDBACK_GENERATION_FAILED);
        }

        pointService.earnPoints(paragraphCompletion.getMember().getId(),100,activityType);
        Feedback savedFeedback = saveFeedback(aiResponse);
        paragraphCompletion.updateFeedback(savedFeedback);
        saveFeedbackScore(aiResponse, savedFeedback);
        return aiResponse;
    }

    private FeedbackResponse generateWritingFeedback(FeedbackRequest request, ActivityType activityType) {
//        Writing writing = writingService.getWriting(request.writingId());
        Writing writing = writingRepository.findById(request.writingId())
                .orElseThrow(() -> new FeedbackException(FeedbackErrorCode.WRITING_NOT_FOUND));
        Topic topic = writing.getTopic();

        // AI에 요청후 JSON 응답을 DTO로 변환
        FeedbackResponse aiResponse = chatClientBuilder.build()
                .prompt("다음은 유저가 선택한 주제입니다." + topic)
                .system(evaluationPromptFeedback)
                .user(writing.getContent())
                .call()
                .entity(FeedbackResponse.class);

        if (aiResponse == null) {
            throw new FeedbackException(FeedbackErrorCode.FEEDBACK_GENERATION_FAILED);
        }

        pointService.earnPoints(writing.getMember().getId(),100,activityType);
        Feedback savedFeedback = saveFeedback(aiResponse);
        writing.updateFeedback(savedFeedback);
        saveFeedbackScore(aiResponse, savedFeedback);
        return aiResponse;
    }

    private Feedback saveFeedback(FeedbackResponse aiResponse) {
        Feedback feedbackToSave = Feedback.builder()
            .content(aiResponse.overallFeedback())
            .expressionStyle(aiResponse.mbtiScore().expressionStyle())
            .contentFormat(aiResponse.mbtiScore().contentFormat())
            .toneOfVoice(aiResponse.mbtiScore().toneOfVoice())
            .substance(aiResponse.evaluationFeedback().substanceFeedback())
            .completeness(aiResponse.evaluationFeedback().completenessFeedback())
            .expressiveness(aiResponse.evaluationFeedback().expressivenessFeedback())
            .clarity(aiResponse.evaluationFeedback().clarityFeedback())
            .coherence(aiResponse.evaluationFeedback().coherenceFeedback())
            .build();

        return feedbackRepository.save(feedbackToSave);
    }

    private void saveFeedbackScore(FeedbackResponse aiResponse, Feedback savedFeedback) {
        FeedbackScore scoreToSave = FeedbackScore.builder()
            .feedbackOverallScore(aiResponse.overallScore())
            .substance_score(aiResponse.evaluation().substance())
            .completeness_score(aiResponse.evaluation().completeness())
            .expressiveness_score(aiResponse.evaluation().expressiveness())
            .clarity_score(aiResponse.evaluation().clarity())
            .coherence_score(aiResponse.evaluation().coherence())
            .feedback(savedFeedback)
            .build();

        FeedbackScore feedbackScore = feedbackScoreRepository.save(scoreToSave);
        savedFeedback.updateFeedbackScore(feedbackScore);
    }

    public SortedMap<LocalDate, Integer> getDailyAverageScoresByDate(String email, String yyyyMM) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new FeedbackException(FeedbackErrorCode.INVALID_MEMBER_ID));

        // Repository에서 원시 데이터 조회
        List<Tuple> rawData = feedbackScoreRepository.findFeedbackScoresByMemberAndDate(member, yyyyMM);

        // yyyy-MM 형식의 문자열을 YearMonth로 파싱
        YearMonth yearMonth = YearMonth.parse(yyyyMM, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 결과를 SortedMap으로 변환
        SortedMap<LocalDate, Integer> scoreMap = new TreeMap<>();

        // 해당 월의 모든 날짜를 0으로 초기화
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            scoreMap.put(currentDate, 0);
            currentDate = currentDate.plusDays(1);
        }

        // 일별 점수 합계와 개수를 계산하기 위한 Map
        Map<LocalDate, Integer> scoreSumMap = new HashMap<>();
        Map<LocalDate, Integer> countMap = new HashMap<>();

        // 원시 데이터 처리
        for (Tuple tuple : rawData) {
            LocalDate date = tuple.get(0, java.sql.Date.class).toLocalDate();
            Integer score = tuple.get(1, Integer.class);

            scoreSumMap.put(date, scoreSumMap.getOrDefault(date, 0) + score);
            countMap.put(date, countMap.getOrDefault(date, 0) + 1);
        }

        // 일별 평균 계산
        for (LocalDate date : scoreSumMap.keySet()) {
            int totalScore = scoreSumMap.get(date);
            int count = countMap.get(date);
            int avgScore = totalScore / count;
            scoreMap.put(date, avgScore);
        }

        return scoreMap;
    }
}
