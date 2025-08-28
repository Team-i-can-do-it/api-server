package com.icando.feedback.controller;

import com.icando.feedback.dto.FeedbackRequest;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.enums.FeedbackSuccessCode;
import com.icando.feedback.service.FeedbackService;
import com.icando.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(
        summary = "AI 피드백 받기",
        description = "피드백 생성 및 저장"
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<FeedbackResponse>> generateFeedback(@Valid @RequestBody FeedbackRequest reqeust) {
        FeedbackResponse feedbackResponse = feedbackService.generateFeedback(reqeust);

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
}
