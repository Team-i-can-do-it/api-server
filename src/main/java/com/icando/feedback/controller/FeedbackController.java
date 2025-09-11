package com.icando.feedback.controller;

import com.icando.feedback.dto.FeedbackRequest;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.enums.FeedbackSuccessCode;
import com.icando.feedback.service.FeedbackService;
import com.icando.global.success.SuccessResponse;
import com.icando.member.entity.ActivityType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.SortedMap;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(
    name = "AI 피드백",
    description = "AI를 통한 피드백을 받을 수 있음"
)
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(
        summary = "AI 피드백 받기",
        description = "피드백 생성 및 저장"
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<FeedbackResponse>> generateFeedback(
            @Valid @RequestBody FeedbackRequest request,
            @RequestParam ActivityType activityType) {
        FeedbackResponse feedbackResponse = feedbackService.generateFeedback(request, activityType);

        final FeedbackSuccessCode successCode =
            FeedbackSuccessCode.FEEDBACK_SUCCESS;

        SuccessResponse<FeedbackResponse> responseBody =
            SuccessResponse.of(
                successCode,
                feedbackResponse
            );

        return ResponseEntity
            .status(successCode.getStatus())
            .body(responseBody);
    }

    @Operation(
            summary = "일별 피드백 평균 점수 조회",
            description = "사용자의 일별 피드백 평균 점수를 조회합니다."
    )
    @GetMapping("/summary/{yyyyMM}")
    public ResponseEntity<SuccessResponse<SortedMap<LocalDate, Integer>>> getDailyAverageScoresByDate(
            @Valid @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])$", message = "yyyy-MM 형식으로 입력해야 합니다.") @PathVariable String yyyyMM,
            @AuthenticationPrincipal UserDetails userDetails) {

        SortedMap<LocalDate, Integer> dailyAverageScores = feedbackService.getDailyAverageScoresByDate(userDetails.getUsername(), yyyyMM);
        return ResponseEntity.ok(SuccessResponse.of(FeedbackSuccessCode.FEEDBACK_SUCCESS_DAILY_AVERAGE_SCORES_RETRIEVED, dailyAverageScores));
    }

}
