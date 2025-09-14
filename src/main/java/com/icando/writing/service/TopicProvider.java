package com.icando.writing.service;

import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import com.icando.writing.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TopicProvider {

    private final TopicRepository topicRepository;

    @Cacheable(cacheNames = "topic", key = "#category")
    public List<Topic> getTopicsByCategory(Category category) {
        return topicRepository.findAllByCategory(category);
    }

    @Cacheable(cacheNames = "topic")
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }
}
