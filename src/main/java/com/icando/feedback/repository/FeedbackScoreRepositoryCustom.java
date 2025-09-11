package com.icando.feedback.repository;

import com.icando.member.entity.Member;
import com.querydsl.core.Tuple;

import java.util.List;

public interface FeedbackScoreRepositoryCustom {
    List<Tuple> findFeedbackScoresByMemberAndDate(Member member, String yyyyMM);
}
