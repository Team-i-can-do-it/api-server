package com.icando.writing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Category {
    
    DAILY_LIFE("daily-life", "일상취미"),
    SOCIAL_POLITICS("social-politics", "사회정치"),
    ECONOMY_BUSINESS("economy-business", "경제비즈니스"),
    TECH_FUTURE("tech-future" , "기술미래"),
    CULTURE_ARTS("culture-arts" , "문화예술"),
    RANDOM("random", "랜덤");

    private final String path;
    private final String description;

    public static Category fromPath(String path) {
        return Arrays.stream(Category.values())
            .filter(category -> category.getPath().equalsIgnoreCase(path))
            .findFirst()
            .orElse(null);
    }
}
