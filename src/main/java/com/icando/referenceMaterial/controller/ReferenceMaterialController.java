package com.icando.referenceMaterial.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.referenceMaterial.dto.ReferenceMaterialListResponse;
import com.icando.referenceMaterial.enums.ReferenceMaterialSuccessCode;
import com.icando.referenceMaterial.service.ReferenceMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reference-material")
@RequiredArgsConstructor
@Tag(
    name = "참고자료 API",
    description = "글쓰기 힌트 같은 같은 참고자료 부분입니다."
)
public class ReferenceMaterialController {
    private final ReferenceMaterialService referenceMaterialService;

    @Operation(
        summary = "참고 자료 조회",
        description = "참고 자료 기반을 조회하는 컨트롤러입니다."
    )
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
