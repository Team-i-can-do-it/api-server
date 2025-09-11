package com.icando.feedback.repository;

import com.icando.feedback.entity.FeedbackScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackScoreRepository extends JpaRepository<FeedbackScore, Long>, FeedbackScoreRepositoryCustom {
}
