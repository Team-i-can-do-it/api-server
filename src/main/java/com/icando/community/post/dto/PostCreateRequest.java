package com.icando.community.post.dto;

import com.icando.community.post.entity.Post;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCreateRequest {

    @NotNull(message = "제목은 필수입니다")
    private String title;

    @NotNull(message = "게시글 내용은 필수입니다")
    private String content;

    public static PostCreateRequest of(Post post) {
        return new PostCreateRequest(
                post.getTitle(),
                post.getContent());
    }

}
