package com.icando.writing.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WritingType {
    WRITING("글쓰기"),
    PARAGRAPH_COMPLETION("문단 맞추기");

    private final String type;
}
