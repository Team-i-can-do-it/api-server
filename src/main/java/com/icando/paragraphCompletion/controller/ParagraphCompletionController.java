package com.icando.paragraphCompletion.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.paragraphCompletion.dto.ParagraphCompletionListResponse;
import com.icando.paragraphCompletion.dto.ParagraphCompletionRequest;
import com.icando.paragraphCompletion.dto.ParagraphCompletionResponse;
import com.icando.paragraphCompletion.enums.ParagraphCompletionErrorCode;
import com.icando.paragraphCompletion.enums.ParagraphCompletionSuccessCode;
import com.icando.paragraphCompletion.exception.ParagraphCompletionException;
import com.icando.paragraphCompletion.service.ParagraphCompletionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paragraph-completion")
@RequiredArgsConstructor
@Tag(
    name = "문단 완성 API",
    description = "문단 완성 관련 API입니다."
)
public class ParagraphCompletionController {
    private final ParagraphCompletionService paragraphCompletionService;

    @Operation(
        summary = "랜덤 단어 반환",
        description = "파라미터의 갯수에 따라 랜덤단어를 반환합니다."
    )
    @GetMapping("/words")
    public ResponseEntity<SuccessResponse<List<String>>> getWords(@RequestParam @Min(3) @Max(100) int count) {
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.RANDOM_WORD_SUCCESS,
                        paragraphCompletionService.generateWords(count)
                )
        );
    }

    @Operation(
        summary = "문단 완성한 글을 받습니다.",
        description = "랜덤 문단으로 완성된 글을 저장합니다."
    )
    @PostMapping()
    public ResponseEntity<SuccessResponse<ParagraphCompletionResponse>> writeParagraphCompletionArticle(@Valid @RequestBody ParagraphCompletionRequest paragraphCompletionRequest,
                                                                                                        @AuthenticationPrincipal UserDetails userDetails) {
        ParagraphCompletionResponse response = paragraphCompletionService.insertParagraphCompletionArticle(userDetails.getUsername(), paragraphCompletionRequest);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.PARAGRAPH_COMPLETION_CREATE_SUCCESS,
                        response
                )
        );
    }

    @Operation(
        summary = "문단 완성된 글을 조회합니다.",
        description = "문단 완성된 글을 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ParagraphCompletionResponse>> getParagraphCompletionArticle(@PathVariable Long id,
                                                                                                      @AuthenticationPrincipal UserDetails userDetails) {
        ParagraphCompletionResponse response = paragraphCompletionService.getParagraphCompletionArticle(userDetails.getUsername(), id);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.PARAGRAPH_COMPLETION_READ_SUCCESS,
                        response
                )
        );
    }

    @Operation(
        summary = "문단 완성 글 전체 조회",
        description = "문단 완성 글 전체를 조회합니다."
    )
    @GetMapping()
    public ResponseEntity<SuccessResponse<Page<ParagraphCompletionListResponse>>> getAllParagraphCompletionArticle(
            @Valid @RequestParam(defaultValue = "20") @Min(1) @Max(100) int pageSize,
            @Valid @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "createdAt,DESC") String sort,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String [] sortParams = sort.split(",");
        if (sortParams.length != 2) {
            throw new ParagraphCompletionException(ParagraphCompletionErrorCode.INVALID_SORT_PARAMETER);
        }
        String sortBy = sortParams[0];
        boolean isAsc = "ASC".equalsIgnoreCase(sortParams[1]);

        Page<ParagraphCompletionListResponse> responses = paragraphCompletionService.getAllParagraphCompletionArticle(userDetails.getUsername(), pageSize, page, sortBy, isAsc);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.PARAGRAPH_COMPLETION_READ_ALL_SUCCESS,
                        responses
                )
        );
    }
}
