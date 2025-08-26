package com.icando.global.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public static <T>PagedResponse<T> of(List<T> content, int page, int pageSize, long totalElements, int totalPages) {
        PagedResponse<T> pagedResponse = new PagedResponse<>();
        pagedResponse.setContent(content);
        pagedResponse.setPage(page);
        pagedResponse.setPageSize(pageSize);
        pagedResponse.setTotalElements(totalElements);
        pagedResponse.setTotalPages(totalPages);
        return pagedResponse;
    }
}
