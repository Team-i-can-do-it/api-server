package com.icando.member.dto;

import com.icando.member.entity.Mbti;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MbtiRecentlyDto {

    private String mbtiName;
    private Long mbtiId;

    public MbtiRecentlyDto(String mbtiName, Long mbtiId) {
        this.mbtiName = mbtiName;
        this.mbtiId = mbtiId;
    }
    public static MbtiRecentlyDto of(String mbtiName, Long mbtiId) {
        return new MbtiRecentlyDto(mbtiName, mbtiId);
    }
}
