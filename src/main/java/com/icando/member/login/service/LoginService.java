package com.icando.member.login.service;

import com.icando.global.utils.RedisUtil;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.login.dto.JoinDto;
import com.icando.member.login.dto.JoinResponse;
import com.icando.member.entity.Member;
import com.icando.member.entity.Role;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final RedisUtil redisUtil;

    public JoinResponse join(JoinDto joinDto) {

        String verified = redisUtil.getData("verified:" + joinDto.getEmail());

        if(verified == null || !verified.equals(joinDto.getEmail())) {
            throw new AuthException(AuthErrorCode.ALREADY_MEMBER_EXIST);
        }
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

        memberRepository.save(member);
        redisUtil.deleteData("verified:" + member.getEmail());

        return JoinResponse.toResponse(member);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws MemberException {
        System.out.println("=== loadUserByUsername 호출됨! email: " + email + " ===");
        log.info("loadUserByUsername 호출됨! email: {}", email);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_INVALID));

        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();
    }
}
