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
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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

        // [수정] 이메일 값이 비어있는 경우에 대한 예외 처리
        if (!StringUtils.hasText(email)) {
            // 여기에 비즈니스 로직에 맞는 예외를 던지거나 다른 처리를 해야 합니다.
            // 예를 들어, OAuth2AuthenticationException을 발생시켜 FailureHandler로 보내기
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider.");
        }

        Optional<Member> findMember = memberRepository.findByEmail(email);

        return findMember.orElseGet(() -> saveMember(attributes, provider));
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

