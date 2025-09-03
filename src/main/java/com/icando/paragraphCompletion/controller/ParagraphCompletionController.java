package com.icando.paragraphCompletion.controller;

import com.icando.global.dto.PagedResponse;
import com.icando.global.success.SuccessResponse;
import com.icando.paragraphCompletion.dto.ParagraphCompletionListResponse;
import com.icando.paragraphCompletion.dto.ParagraphCompletionRequest;
import com.icando.paragraphCompletion.dto.ParagraphCompletionResponse;
import com.icando.paragraphCompletion.enums.ParagraphCompletionSuccessCode;
import com.icando.paragraphCompletion.service.ParagraphCompletionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paragraph-completion")
@RequiredArgsConstructor
public class ParagraphCompletionController {
    private final ParagraphCompletionService paragraphCompletionService;

    @GetMapping("/words")
    public ResponseEntity<SuccessResponse<List<String>>> getWords(@RequestParam @Min(3) @Max(100) int count) {
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.RANDOM_WORD_SUCCESS,
                        paragraphCompletionService.generateWords(count)
                )
        );
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse<ParagraphCompletionResponse>> writeParagraphCompletionArticle(@Valid @RequestBody ParagraphCompletionRequest paragraphCompletionRequest,
                                                                                                        @AuthenticationPrincipal UserDetails userDetails) {
        //TODO: 현재는 1으로 고정, 추후에 UserDetails에서 MemberId를 가져와야 함
        ParagraphCompletionResponse response = paragraphCompletionService.insertParagraphCompletionArticle(userDetails.getUsername(), paragraphCompletionRequest);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.PARAGRAPH_COMPLETION_CREATE_SUCCESS,
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ParagraphCompletionResponse>> getParagraphCompletionArticle(@PathVariable Long id,
                                                                                                      @AuthenticationPrincipal UserDetails userDetails) {
        //TODO: 현재는 1으로 고정, 추후에 UserDetails에서 MemberId를 가져와야 함
        ParagraphCompletionResponse response = paragraphCompletionService.getParagraphCompletionArticle(userDetails.getUsername(), id);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.PARAGRAPH_COMPLETION_READ_SUCCESS,
                        response
                )
        );
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse<PagedResponse<ParagraphCompletionListResponse>>> getAllParagraphCompletionArticle(
            @Valid @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @Valid @RequestParam(defaultValue = "1") @Min(1) int page,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        PagedResponse<ParagraphCompletionListResponse> responses = paragraphCompletionService.getAllParagraphCompletionArticle(userDetails.getUsername(), pageSize, page);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.PARAGRAPH_COMPLETION_READ_ALL_SUCCESS,
                        responses
                )
        );
    }
}
