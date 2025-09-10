package com.icando.feedback.repository;

import com.icando.feedback.entity.FeedbackScore;
import com.icando.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.SortedMap;

public interface FeedbackScoreRepository extends JpaRepository<FeedbackScore, Long>, FeedbackScoreRepositoryCustom {
}
