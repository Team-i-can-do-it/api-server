package com.icando.community;

import com.icando.community.post.dto.PostCreateRequest;
import com.icando.community.post.entity.Post;
import com.icando.community.post.repository.PostRepository;
import com.icando.community.post.service.PostService;

import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
public class PostTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member user;

    @BeforeEach
    void setUp() {

        user = Member.of("user1","user@example.com","1234");
        memberRepository.save(user);

        String title = "test";
        String content ="test content";

        PostCreateRequest createRequest = new PostCreateRequest(title,content);
    }

    @Test
    public void 게시글_생성() throws Exception {
        //given

        String title = "test";
        String content ="test content";
        PostCreateRequest createRequest = new PostCreateRequest(title,content);

        //when
        Post createdPost = postService.createPost(createRequest,user.getId());

        //then
        assertThat(createdPost.getTitle()).isEqualTo("test");
        assertThat(createdPost.getContent()).isEqualTo("test content");
        assertThat(createdPost.getMember().getId()).isEqualTo(user.getId());
        }



}
