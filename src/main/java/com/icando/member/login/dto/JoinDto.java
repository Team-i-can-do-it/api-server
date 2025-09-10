package com.icando.member.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class JoinDto {

    //TODO : 제약조건 추가

    private String name;
    private String email;
    private String password;

}
