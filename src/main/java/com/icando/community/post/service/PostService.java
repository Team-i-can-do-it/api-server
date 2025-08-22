package com.icando.community.post.service;

import com.icando.community.post.dto.PostCreateRequest;
import com.icando.community.post.entity.Post;
import com.icando.community.post.repository.PostRepository;
import com.icando.member.entity.Member;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Post createPost(PostCreateRequest postCreateRequest,Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_MEMBER_ID));

        Post post = Post.of(postCreateRequest.getTitle(), postCreateRequest.getContent(),member);

        postRepository.save(post);

        return post;
    }
}
