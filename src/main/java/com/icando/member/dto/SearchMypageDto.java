package com.icando.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchMypageDto {

    private String name;
    private int point;

    private SearchMypageDto(String name, int point) {
        this.name = name;
        this.point = point;
    }

    public static SearchMypageDto of(String name, int point) {
        return new SearchMypageDto(name, point);
    }

}
