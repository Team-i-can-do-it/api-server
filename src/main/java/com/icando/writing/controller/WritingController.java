package com.icando.writing.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.writing.dto.TopicResponse;
import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import com.icando.writing.enums.WritingSuccessCode;
import com.icando.writing.error.TopicErrorCode;
import com.icando.writing.error.TopicException;
import com.icando.writing.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/writing")
@RequiredArgsConstructor
@Tag(name = "글쓰기 API", description = "주제 선정 등 글쓰기 관련 API")
public class WritingController {

    private final ChatClient.Builder ai;
    private final TopicService topicService;

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

        TopicResponse topicResponse = new TopicResponse(selectedTopic.getTopic());

        SuccessResponse<TopicResponse> responseBody =
            SuccessResponse.of(WritingSuccessCode.TOPIC_SELECT_SUCCESS, topicResponse);

        return ResponseEntity
            .status(WritingSuccessCode.TOPIC_SELECT_SUCCESS.getStatus())
            .body(responseBody);
    }

}
