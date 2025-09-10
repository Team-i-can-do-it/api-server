package com.icando.feedback.repository;

import com.icando.member.entity.Member;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.icando.feedback.entity.QFeedback.feedback;
import static com.icando.feedback.entity.QFeedbackScore.feedbackScore;
import static com.icando.writing.entity.QWriting.writing;
import static com.icando.paragraphCompletion.entity.QParagraphCompletion.paragraphCompletion;

@RequiredArgsConstructor
public class FeedbackScoreRepositoryImpl implements FeedbackScoreRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Tuple> findFeedbackScoresByMemberAndDate(Member member, String yyyyMM) {
        // yyyy-MM 형식의 문자열을 YearMonth로 파싱
        YearMonth yearMonth = YearMonth.parse(yyyyMM, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Writing을 통한 피드백 점수 조회
        List<Tuple> writingResults = queryFactory
            .select(
                Expressions.dateTemplate(LocalDate.class, "DATE({0})", writing.createdAt),
                feedbackScore.feedbackOverallScore
            )
            .from(writing)
            .join(writing.feedback, feedback)
            .join(feedback.feedbackScore, feedbackScore)
            .where(
                writing.member.eq(member)
                .and(Expressions.dateTemplate(LocalDate.class, "DATE({0})", writing.createdAt)
                    .between(startDate, endDate))
            )
            .fetch();

        // ParagraphCompletion을 통한 피드백 점수 조회
        List<Tuple> paragraphResults = queryFactory
            .select(
                Expressions.dateTemplate(LocalDate.class, "DATE({0})", paragraphCompletion.createdAt),
                feedbackScore.feedbackOverallScore
            )
            .from(paragraphCompletion)
            .join(paragraphCompletion.feedback, feedback)
            .join(feedback.feedbackScore, feedbackScore)
            .where(
                paragraphCompletion.member.eq(member)
                .and(Expressions.dateTemplate(LocalDate.class, "DATE({0})", paragraphCompletion.createdAt)
                    .between(startDate, endDate))
            )
            .fetch();

        // 두 결과를 합쳐서 반환
        writingResults.addAll(paragraphResults);
        return writingResults;
    }
}
