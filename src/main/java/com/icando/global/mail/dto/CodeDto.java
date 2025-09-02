package com.icando.global.mail.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeDto {

    private String email;
    @NotNull @Size(min = 6, max = 6)
    private String code;
}
