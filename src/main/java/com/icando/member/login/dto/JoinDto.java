package com.icando.member.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinDto {

    //TODO : 제약조건 추가

    private String name;
    private String email;
    private String password;

}
