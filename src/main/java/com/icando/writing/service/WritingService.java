package com.icando.writing.service;

import com.icando.feedback.dto.FeedbackRequest;
import com.icando.feedback.dto.FeedbackResponse;
import com.icando.feedback.service.FeedbackService;
import com.icando.member.entity.ActivityType;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.entity.Member;
import com.icando.member.repository.MemberRepository;
import com.icando.writing.dto.WritingCreateRequest;
import com.icando.writing.dto.WritingListResponse;
import com.icando.writing.dto.WritingResponse;
import com.icando.writing.entity.Topic;
import com.icando.writing.entity.Writing;
import com.icando.writing.enums.WritingType;
import com.icando.writing.error.TopicErrorCode;
import com.icando.writing.error.TopicException;
import com.icando.writing.error.WritingErrorCode;
import com.icando.writing.error.WritingException;
import com.icando.writing.repository.TopicRepository;
import com.icando.writing.repository.WritingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WritingService {

    private final WritingRepository writingRepository;
    private final MemberRepository memberRepository;
    private final TopicRepository topicRepository;
    private final FeedbackService feedbackService;

    @Transactional
    public WritingResponse createWriting(WritingCreateRequest request, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_MEMBER_ID));

        Topic topic = topicRepository.findById(request.topicId())
            .orElseThrow(() -> new TopicException(TopicErrorCode.TOPIC_NOT_FOUND));

        Writing writing = Writing.of(request.content(), member, topic);

        writing = writingRepository.save(writing);

        feedbackService.generateFeedback(new FeedbackRequest(WritingType.WRITING, writing.getId()), ActivityType.TOPIC);

        return WritingResponse.of(writingRepository.findById(writing.getId()).orElseThrow(() -> new WritingException(WritingErrorCode.WRITING_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public Writing getWriting(Long writingId) {
        return writingRepository.findById(writingId)
            .orElseThrow(() -> new WritingException(WritingErrorCode.WRITING_NOT_FOUND));
    }

    public Page<WritingListResponse> getAllWritings(String username, int pageSize, int page, String sortBy, boolean isAsc) {

        PageRequest pageRequest = PageRequest.of(page, pageSize, isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_MEMBER_ID));
        Page<Writing> result = writingRepository.findAllByMember(member, pageRequest);

        return result
                .map(writing -> new WritingListResponse(
                        writing.getId(),
                        writing.getTopic().getTopicContent(),
                        (writing.getContent().length() > 200 ? writing.getContent().substring(0, 200) + "..." : writing.getContent()),
                        writing.getFeedback().getExpressionStyle(),
                        writing.getFeedback().getContentFormat(),
                        writing.getFeedback().getToneOfVoice(),
                        writing.getFeedback().getFeedbackScore().getFeedbackOverallScore(),
                        writing.getCreatedAt()
                ));
    }

    public WritingResponse getWritingResponse(Long id) {

        Writing writing = writingRepository.findById(id)
            .orElseThrow(() -> new WritingException(WritingErrorCode.WRITING_NOT_FOUND));

        return WritingResponse.of(writing);
    }
}
