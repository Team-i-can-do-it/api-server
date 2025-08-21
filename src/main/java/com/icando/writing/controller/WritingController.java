package com.icando.writing.controller;

import com.icando.global.success.SuccessResponse;
import com.icando.writing.dto.TopicResponse;
import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import com.icando.writing.enums.WritingSuccessCode;
import com.icando.writing.error.TopicErrorCode;
import com.icando.writing.error.TopicException;
import com.icando.writing.service.TopicService;
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
public class WritingController {

    private final ChatClient.Builder ai;
    private final TopicService topicService;

    @GetMapping("/topics/{category}")
    public ResponseEntity<SuccessResponse<TopicResponse>> getTopicByCategory(@PathVariable String category) {

        if ("random".equalsIgnoreCase(category)) {
            Topic randomTopic = topicService.getRandomTopic();
            TopicResponse topicResponse = new TopicResponse(randomTopic.getTopic());
            SuccessResponse<TopicResponse> responseBody = SuccessResponse.of(
                WritingSuccessCode.TOPIC_SELECT_SUCCESS,
                topicResponse
            );

            return ResponseEntity
                .status(WritingSuccessCode.TOPIC_SELECT_SUCCESS.getStatus())
                .body(responseBody);
        }

        Category selectedCategory = Category.fromPath(category);

        if (selectedCategory == null) {
            throw new TopicException(TopicErrorCode.TOPIC_NOT_FOUND);
        }

        TopicResponse topicResponse = getRandomTopic(selectedCategory);

        SuccessResponse<TopicResponse> responseBody =
            SuccessResponse.of(WritingSuccessCode.TOPIC_SELECT_SUCCESS, topicResponse);

        return ResponseEntity
            .status(WritingSuccessCode.TOPIC_SELECT_SUCCESS.getStatus())
            .body(responseBody);
    }

    private TopicResponse getRandomTopic(Category category) {
        return new TopicResponse(topicService.getRandomTopicByCategory(category).getTopic());
    }

}
