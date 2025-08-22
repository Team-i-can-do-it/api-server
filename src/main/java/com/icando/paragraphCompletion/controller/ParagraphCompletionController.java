package com.icando.paragraphCompletion.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.paragraphCompletion.enums.ParagraphCompletionSuccessCode;
import com.icando.paragraphCompletion.service.ParagraphCompletionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/paragraph-completion")
@RequiredArgsConstructor
public class ParagraphCompletionController {
    private final ParagraphCompletionService paragraphCompletionService;

    @GetMapping("/words")
    public ResponseEntity<SuccessResponse<List<String>>> getWords(@RequestParam @Valid @Min(3) @Max(100) int count) {
        return ResponseEntity.ok(
                SuccessResponse.of(
                        ParagraphCompletionSuccessCode.RANDOM_WORD_SUCCESS,
                        paragraphCompletionService.generateWords(count)
                )
        );
    }
}
