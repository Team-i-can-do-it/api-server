package com.icando.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class MyPageResponse {

    private String name;
    private int point;
    private MbtiRecentlyDto recently;

    public static MyPageResponse of(SearchMypageDto dto, MbtiRecentlyDto recently) {
        return new MyPageResponse(
                dto.getName(),
                dto.getPoint(),
                recently
        );
    }
}
