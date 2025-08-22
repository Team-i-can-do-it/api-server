package com.icando.community.post.controller;

import com.icando.community.post.dto.PostCreateRequest;
import com.icando.community.post.exception.PostSuccessCode;
import com.icando.community.post.service.PostService;
import com.icando.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //TODO: 추후 auth 관련 로직 개발 뒤 @AuthenticationPrincipal 사용 예정
    @PostMapping
    public ResponseEntity<SuccessResponse> createPost(
            @RequestBody @Valid PostCreateRequest postCreateRequest,
            @RequestParam Long memberId) {

        postService.createPost(postCreateRequest,memberId);
        return ResponseEntity.ok(
                SuccessResponse.of(PostSuccessCode.SUCCESS_CREATE_POST));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<SuccessResponse> selectPost(
            @PathVariable Long postId) {

        postService.selectPostById(postId);
        return ResponseEntity.ok(
                SuccessResponse.of(PostSuccessCode.SUCCESS_SELECT_POST));
    }


}
