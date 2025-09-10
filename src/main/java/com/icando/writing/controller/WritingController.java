package com.icando.writing.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.writing.dto.TopicResponse;
import com.icando.writing.dto.WritingCreateRequest;
import com.icando.writing.dto.WritingListResponse;
import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import com.icando.writing.enums.WritingSuccessCode;
import com.icando.writing.error.TopicErrorCode;
import com.icando.writing.error.TopicException;
import com.icando.writing.error.WritingErrorCode;
import com.icando.writing.error.WritingException;
import com.icando.writing.service.TopicService;
import com.icando.writing.service.WritingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/writing")
@RequiredArgsConstructor
@Tag(
    name = "글쓰기 API",
    description = "주제 선정 등 글쓰기 관련 API"
)
public class WritingController {

    private final TopicService topicService;
    private final WritingService writingService;

    @Operation(
        summary = "랜덤 주제 조회",
        description = "카테고리별 또는 전체 랜덤 주제 1개를 조회합니다."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "주제 조회 성공"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 카테고리",
                content = @Content
            )
        }
    )
    @GetMapping("/topics/{category}")
    public ResponseEntity<SuccessResponse<TopicResponse>> getTopicByCategory(@PathVariable String category) {
        Topic selectedTopic;

        if ("random".equalsIgnoreCase(category)) {
            selectedTopic = topicService.getRandomTopic();
        } else {
            Category selectedCategory = Category.fromPath(category);
            if (selectedCategory == null) {
                throw new TopicException(TopicErrorCode.TOPIC_NOT_FOUND);
            }
            selectedTopic = topicService.getRandomTopicByCategory(selectedCategory);

        }

        TopicResponse topicResponse = new TopicResponse(selectedTopic.getTopicContent(), selectedTopic.getTopicTitle(), selectedTopic.getTopicDescription());

        SuccessResponse<TopicResponse> responseBody =
            SuccessResponse.of(WritingSuccessCode.TOPIC_SELECT_SUCCESS, topicResponse);

        return ResponseEntity
            .status(WritingSuccessCode.TOPIC_SELECT_SUCCESS.getStatus())
            .body(responseBody);
    }

    @Operation(
        summary = "글 저장",
        description = "새로운 글을 저장합니다."
    )
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createWriting(
            @RequestBody @Valid WritingCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        writingService.createWriting(request, userDetails.getUsername());

        return ResponseEntity
            .ok(SuccessResponse.of(WritingSuccessCode.WRITING_CREATE_SUCCESS));
    }

    @Operation(
        summary = "글쓰기 내역 리스트 조회",
        description = "유저는 자기의 글쓰기 내역을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<WritingListResponse>>> getAllWritings(
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "createdAt,DESC") String sort,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // TODO: sort 형식을 통일하는 방향대로 수정할 것
        String [] sortParams = sort.split(",");
        if (sortParams.length != 2) {
            throw new WritingException(WritingErrorCode.INVALID_SORT_PARAMETER);
        }
        String sortBy = sortParams[0];
        boolean isAsc = "ASC".equalsIgnoreCase(sortParams[1]);

        Page<WritingListResponse> responses = writingService.getAllWritings(userDetails.getUsername(), pageSize, page, sortBy, isAsc);
        return ResponseEntity.ok(
                SuccessResponse.of(
                        WritingSuccessCode.WRITING_READ_ALL_SUCCESS,
                        responses
                )
        );
    }
}
