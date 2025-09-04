package com.icando.writing.service;

import com.icando.feedback.dto.FeedbackResponse;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.writing.dto.TopicResponse;
import com.icando.writing.dto.WritingCreateRequest;
import com.icando.writing.dto.WritingListResponse;
import com.icando.writing.entity.Topic;
import com.icando.writing.entity.Writing;
import com.icando.writing.error.TopicErrorCode;
import com.icando.writing.error.TopicException;
import com.icando.writing.error.WritingErrorCode;
import com.icando.writing.error.WritingException;
import com.icando.writing.repository.TopicRepository;
import com.icando.writing.repository.WritingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WritingService {

    private final WritingRepository writingRepository;
    private final MemberRepository memberRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public void createWriting(WritingCreateRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_MEMBER_ID));

        Topic topic = topicRepository.findById(request.topicId())
            .orElseThrow(() -> new TopicException(TopicErrorCode.TOPIC_NOT_FOUND));

        Writing writing = Writing.of(request.content(), member, topic);

        writingRepository.save(writing);
    }

    @Transactional(readOnly = true)
    public Writing getWriting(Long writingId) {
        return writingRepository.findById(writingId)
            .orElseThrow(() -> new WritingException(WritingErrorCode.WRITING_NOT_FOUND));
    }

    public Page<Writing> getAllWritings(String username, int pageSize, int page) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_MEMBER_ID));

        return writingRepository.findAllByMember(member, pageRequest)
                .map(writing -> new WritingListResponse(
                        writing.getId(),
                        FeedbackResponse.of(writing.getFeedback()),
                        TopicResponse.of(writing.getTopic())
                ));
    }
}
