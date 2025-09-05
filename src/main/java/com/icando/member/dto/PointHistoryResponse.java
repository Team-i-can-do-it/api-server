package com.icando.member.dto;

import com.icando.member.entity.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PointHistoryResponse {

    private LocalDateTime createdAt;
    private ActivityType activityType;
    private int points;

    public static PointHistoryResponse of(LocalDateTime createdAt, ActivityType activityType, int points) {
        return new PointHistoryResponse(createdAt, activityType, points);
    }

}
