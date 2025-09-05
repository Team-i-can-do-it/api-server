package com.icando.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageResponse {

    private String name;
    private int point;
    private Long mbtiId;
    private String mbtiName;

    public static MyPageResponse of(String name, int point, Long mbtiId, String mbtiName) {
        return new MyPageResponse(name, point, mbtiId, mbtiName);
    }
}
