package com.icando.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MbtiResponse {

    private Long RecentlyMbtiId;
    private String RecentlyMbtiName;
    private List<Object[]> mbtiList;

    public static MbtiResponse of(Long RecentlyMbtiId, String RecentlyMbtiName, List<Object[]> mbtiList) {
        return new MbtiResponse(RecentlyMbtiId, RecentlyMbtiName, mbtiList);
    }

}
