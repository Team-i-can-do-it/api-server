package com.icando.member.login.service;

import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.login.dto.JoinDto;
import com.icando.member.login.dto.JoinResponse;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public JoinResponse join(JoinDto joinDto) {

        // EMAIL이 이미 존재하면 ERROR 예외 발생
        memberRepository.findByEmail(joinDto.getEmail())
                .ifPresent(member -> {
                    throw new AuthException(AuthErrorCode.MEMBER_ALREADY_EXIST);
                });

        // DTO를 통해 Member객체 생성
        Member member = Member.createLocalMember(
                joinDto.getName(),
                joinDto.getEmail(),
                bCryptPasswordEncoder.encode(joinDto.getPassword()),
                Role.valueOf(Role.USER.name()),
                false
        );

        if(member.getIsVerified() == true) {
            throw new AuthException(AuthErrorCode.EMAIL_INVALID);
        }

        memberRepository.save(member);

        return JoinResponse.toResponse(member);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws MemberException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_INVALID));

        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();
    }

    






}
