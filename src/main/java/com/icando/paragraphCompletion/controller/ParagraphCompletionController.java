package com.icando.paragraphCompletion.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.paragraphCompletion.dto.ParagraphCompletionRequest;
import com.icando.paragraphCompletion.dto.ParagraphCompletionResponse;
import com.icando.paragraphCompletion.enums.ParagraphCompletionSuccessCode;
import com.icando.paragraphCompletion.service.ParagraphCompletionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SuccessResponse<ParagraphCompletionResponse>> writeParagraphCompletionArticle(@Valid @RequestBody ParagraphCompletionRequest paragraphCompletionRequest) {
        //TODO: 현재는 1으로 고정, 추후에 UserDetails에서 MemberId를 가져와야 함
        ParagraphCompletionResponse response = paragraphCompletionService.insertParagraphCompletionArticle(1L, paragraphCompletionRequest);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.PARAGRAPH_COMPLETION_CREATE_SUCCESS,
                        response
                )
        );
    }
}
