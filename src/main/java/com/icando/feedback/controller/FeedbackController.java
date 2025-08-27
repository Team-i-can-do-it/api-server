package com.icando.feedback.controller;

import com.icando.feedback.dto.FeedbackReqeust;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.enums.FeedbackSuccessCode;
import com.icando.feedback.service.FeedbackService;
import com.icando.global.success.SuccessResponse;
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

    @PostMapping
    public ResponseEntity<SuccessResponse<FeedbackResponse>> generateFeedback(@Valid @RequestBody FeedbackReqeust reqeust) {
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
