package com.icando.writing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Category {
    DAILY_LIFE("일상취미"),
    SOCIAL_POLITICS("사회정치"),
    ECONOMY_BUSINESS("경제비즈니스"),
    TECH_FUTURE("기술미"),
    CULTURE_ARTS("문화예술"),
    RANDOM("랜덤");

    private final String topic;

}
