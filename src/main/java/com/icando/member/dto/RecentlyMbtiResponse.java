package com.icando.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentlyMbtiResponse {
    private Long mbtiId;
    private String mbtiName;

    public static RecentlyMbtiResponse of(Long mbtiId, String mbtiName) {
        return new RecentlyMbtiResponse(mbtiId, mbtiName);
    }
}
