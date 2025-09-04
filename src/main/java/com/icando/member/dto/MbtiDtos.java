package com.icando.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MbtiDtos {

    private String mbtiName;
    private Long mbtiId;

    private MbtiDtos(String mbtiName, Long mbtiId) {
        this.mbtiName = mbtiName;
        this.mbtiId = mbtiId;
    }

    public static MbtiDtos of(String mbtiName, Long mbtiId) {
        return new MbtiDtos(mbtiName, mbtiId);
    }
}
