package com.icando.writing.service;

import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import com.icando.writing.error.TopicErrorCode;
import com.icando.writing.error.TopicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicProvider topicProvider;

    // 카테고리 랜덤
    public Topic getRandomTopicByCategory(Category category) {
        List<Topic> topics = topicProvider.getTopicsByCategory(category);
        checkTopic(topics);
        int randomIndex = randomIndex(topics.size());

        return topics.get(randomIndex);
    }

    // 주제 전체 랜덤
    public Topic getRandomTopic() {
        List<Topic> topics = topicProvider.getAllTopics();
        checkTopic(topics);
        int randomIndex = randomIndex(topics.size());

        return topics.get(randomIndex);
    }

    private void checkTopic(List<Topic> topics) {
        if (topics == null || topics.isEmpty()) {
            throw new TopicException(TopicErrorCode.TOPIC_NOT_FOUND);
        }
    }

    private int randomIndex(int size) {
        return ThreadLocalRandom.current().nextInt(size);
    }

}
