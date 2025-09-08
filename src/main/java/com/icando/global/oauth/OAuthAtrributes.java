package com.icando.global.oauth;

import com.icando.global.oauth.userinfo.GoogleOAuth2UserInfo;
import com.icando.global.oauth.userinfo.NaverOAuth2UserInfo;
import com.icando.global.oauth.userinfo.OAuth2UserInfo;
import com.icando.member.entity.Member;
import com.icando.member.entity.Provider;
import com.icando.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAtrributes {

    // OAuth2 로그인 시 key가 되는 값(PK)
    private String nameAttributeKey;
    //소셜 타입별 로그인 유저 정보
    private OAuth2UserInfo oAuth2UserInfo;
    private String name;

    @Builder
    private OAuthAtrributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo, String name) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
        this.name = name;
    }

    public static OAuthAtrributes of(Provider provider, String userNameAttributeName,
                                     Map<String, Object> attributes) {
            if(provider == Provider.NAVER) {
                return ofNaver(userNameAttributeName, attributes);
            }
            return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuthAtrributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAtrributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .name((String) attributes.get("name"))
                .build();
    }

    public static OAuthAtrributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAtrributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .name((String) attributes.get("name"))
                .build();
    }

    public Member toEntity(Provider provider,OAuth2UserInfo oAuth2UserInfo) {
        return Member.builder()
                .provider(provider)
                .providerId(oAuth2UserInfo.getId())
                .email(oAuth2UserInfo.getEmail())
                .name(this.name)
                .role(Role.USER)
                .build();
    }


}
