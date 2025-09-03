package com.icando.referenceMaterial.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.referenceMaterial.dto.ReferenceMaterialAiResponse;
import com.icando.referenceMaterial.enums.ReferenceMaterialSuccessCode;
import com.icando.referenceMaterial.service.ReferenceMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reference-material")
@RequiredArgsConstructor
public class ReferenceMaterialController {
    private final ReferenceMaterialService referenceMaterialService;

    @GetMapping("/test")
    public ResponseEntity<SuccessResponse<List<ReferenceMaterialAiResponse>>> test(@RequestParam("topicId") long topicId) {
        List<ReferenceMaterialAiResponse> responses = referenceMaterialService.getReferenceMaterials(topicId);
        SuccessResponse<List<ReferenceMaterialAiResponse>> responseBody =
            SuccessResponse.of(
                    ReferenceMaterialSuccessCode.GENERATE_SUCCESS,
                responses
            );

        return ResponseEntity
            .ok()
            .body(responseBody);
    }
}
