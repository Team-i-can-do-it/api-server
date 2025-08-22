package com.icando.writing.service;

import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import com.icando.writing.error.TopicErrorCode;
import com.icando.writing.error.TopicException;
import com.icando.writing.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    // 카테고리 랜덤
    public Topic getRandomTopicByCategory(Category category) {
        return topicRepository.findRandomByCategory(category.name())
            .orElseThrow(() -> new TopicException(TopicErrorCode.TOPIC_NOT_FOUND));
    }

    // 주제 전체 랜덤
    public Topic getRandomTopic() {
        return topicRepository.findRandom()
            .orElseThrow(() -> new TopicException(TopicErrorCode.TOPIC_NOT_FOUND));
    }


}
