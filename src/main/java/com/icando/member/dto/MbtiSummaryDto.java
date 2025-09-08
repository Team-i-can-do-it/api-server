package com.icando.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MbtiSummaryDto {
    private Long mbtiId;
    private String mbtiName;

    public MbtiSummaryDto(Long id, String name) {
        this.mbtiId = id;
        this.mbtiName = name;
    }
}
