package com.icando.member.service;

import com.icando.member.dto.MbtiRequest;
import com.icando.member.entity.Mbti;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MbtiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MbtiService {

    private final MbtiRepository mbtiRepository;

    @Transactional
    public void saveMbti(MbtiRequest mbtiRequest) {
        Mbti mbti = Mbti.of(
            mbtiRequest.name(),
            mbtiRequest.description(),
            mbtiRequest.imageUrl()
        );

        // TODO: 글로벌 Exception 추후 고려
        try {
            mbtiRepository.save(mbti);
        } catch (DataIntegrityViolationException e) {
            throw new MemberException(MemberErrorCode.MBTI_SAVE_FAILED, e);
        }
    }

}
