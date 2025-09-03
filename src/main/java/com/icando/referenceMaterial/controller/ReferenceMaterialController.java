package com.icando.referenceMaterial.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.referenceMaterial.dto.ReferenceMaterialListResponse;
import com.icando.referenceMaterial.enums.ReferenceMaterialSuccessCode;
import com.icando.referenceMaterial.service.ReferenceMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reference-material")
@RequiredArgsConstructor
public class ReferenceMaterialController {
    private final ReferenceMaterialService referenceMaterialService;

    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<SuccessResponse<List<ReferenceMaterialListResponse>>> getReferenceMaterialByTopicId(@PathVariable Long topicId) {
        List<ReferenceMaterialListResponse> responses = referenceMaterialService.getReferenceMaterialsByTopicId(topicId);
        SuccessResponse<List<ReferenceMaterialListResponse>> responseBody =
            SuccessResponse.of(
                    ReferenceMaterialSuccessCode.REFERENCE_MATERIAL_READ_SUCCESS,
                responses
            );

        return ResponseEntity
            .ok()
            .body(responseBody);
    }
}
