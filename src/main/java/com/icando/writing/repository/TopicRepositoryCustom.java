package com.icando.writing.repository;

import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;

import java.util.Optional;

public interface TopicRepositoryCustom {
    Optional<Topic> findRandomByCategory(Category category);
    Optional<Topic> findRandom();
}
