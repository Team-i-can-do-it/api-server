package com.icando.global.oauth.services;

import com.icando.global.oauth.CustomOAuth2User;
import com.icando.global.oauth.OAuthAttributes;
import com.icando.member.entity.Member;
import com.icando.member.entity.Provider;
import com.icando.member.login.exception.AuthErrorCode;
import com.icando.member.login.exception.AuthException;
import com.icando.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.icando.member.entity.Provider.NAVER;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    public static final String NAVER = "naver";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = getProvider(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes extractAttributes = OAuthAttributes.of(provider, userNameAttributeName, attributes);

        Member createMember = getMember(extractAttributes, provider);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createMember.getRole().name())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createMember.getEmail(),
                createMember.getRole()
        );
    }



    private Member getMember(OAuthAttributes attributes, Provider provider) {

        String email = attributes.getOAuth2UserInfo().getEmail();

        // 1. 이메일로 기존 가입 사용자인지 확인
        Member findMember = memberRepository.findByEmail(email).orElse(null);

        // 2. 이미 가입된 회원이라면, 회원 정보를 그대로 반환 (로그인 성공)
        if (findMember != null) {
            return findMember;
        }

        // 3. 가입되지 않은 회원이라면, 소셜 정보를 기반으로 새로 저장하고 반환
        return saveMember(attributes, provider);
    }


    private Member saveMember(OAuthAttributes attributes, Provider provider) {

            Member createdMember = attributes.toEntity(provider, attributes.getOAuth2UserInfo());
            return memberRepository.save(createdMember);
        }

    private Provider getProvider(String registrationId) {
    if (NAVER.equals(registrationId)) {
        return Provider.NAVER;
    }
    return Provider.GOOGLE;
    }
}

