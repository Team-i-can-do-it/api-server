package com.icando.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.icando.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class JwtService {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${spring.jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${spring.jwt.access.header}")
    private String accessHeader;

    @Value("${spring.jwt.refresh.header}")
    private String refreshHeader;

    @PostConstruct
    public void checkSecret() {
        log.info(">>> JWT SECRET = [{}]", secretKey);
    }

    /**
     * JWT의 Subject와 Claim으로 email사용 -> 클레임 Name = email로 설정
     * JWT Header에 들어오는 값 : Authorization(Key) = Bearer {token} (Value)
     */
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private static final String ACCESS_PREFIX = "blackList:";
    private static final String REFRESH_PREFIX = "RT:";

    private final MemberRepository memberRepository;
    private final RedisTemplate<String ,String> redisTemplate;


    //AccessToken 생성 메서드
    public String createAccessToken(String email) {
        Date now = new Date();
        return JWT.create() // JWT토큰을 생성하는 빌더를 반환
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) //토큰 만료 시간
                // 클레임으로 email만 사용 추가로 더 작성해도 됨
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKey)); //yml파일에서 지정한 secretkey로 암호화

    }

    /**
     * RefreshToken 생성, Claim에 email도 넣지 않는다.
     */
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod ))
                .sign(Algorithm.HMAC512(secretKey));
    }

    //AccessToken을 Header에 실어서 보내기
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, BEARER + accessToken);
        log.info("재발급된 AccessToken : {}", accessToken);
    }

    //Access + Refresh 토큰 Header에 보내기
    public void sendAccessTokenAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, BEARER + accessToken);
        response.setHeader(refreshHeader, BEARER + refreshToken);
        log.info("Access, RefreshToken 설정 완료");
    }

    /**
     * AccessToken 헤더에서 추출 + Bearer에서 순수 토큰만 가져오기 위해서
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    /**
     * RefreshToken 헤더에서 추출 + Bearer에서 순수 토큰만 가져오기 위해서
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * AccessToken에서 email 추출
     * 추출 전 JWT.require()로 검증기 생성
     * verify로 검증 후 유효하면 이메일 추출
     * 유효하지 않다면 빈 Optional 반환
     */
    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(EMAIL_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    //AccessHeader 설정
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, BEARER + accessToken);
    }

    //RefreshToken Header설정
    public void setRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, BEARER + refreshToken);
    }

    //Refresh토큰 Redis 저장
    @Transactional
    public void updateRefreshToken(String email, String refreshToken) {
        log.info("email: {}", email);
        log.info("refreshToken: {}", refreshToken);

        // Redis Key: "RT:{email}"  (RT = RefreshToken prefix)
        redisTemplate.opsForValue()
                .set("RT:" + email, refreshToken, Duration.ofMillis(refreshTokenExpirationPeriod));
    }


    public boolean isTokenValid(String token) {
        try {
            // 블랙리스트 확인
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            if (ops.get(ACCESS_PREFIX + token) != null) {
                log.info("로그아웃 된 토큰입니다.");
                return false;
            }

            // 토큰 검증
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);


            // 만료 확인
            return decodedJWT.getExpiresAt().after(new Date());
        } catch (Exception e) {
            log.error("유효하지 않은 Token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token, String email) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);

            // Redis 저장된 토큰 확인
            String storedToken = redisTemplate.opsForValue().get(REFRESH_PREFIX + email);
            return storedToken != null && storedToken.equals(token)
                    && decodedJWT.getExpiresAt().after(new Date());
        } catch (Exception e) {
            log.error("유효하지 않은 RefreshToken: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 만료시간 가져오기
     */
    public Date getExpiration(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
        return decodedJWT.getExpiresAt();
    }

    /**
     * 로그아웃
     * AccessToken 남은 만료시간 계산 후 레디스에 블랙리스트 저장
     * 레디스에 저장된 RefreshToken 삭제
     */
    @Transactional
    public void logout(HttpServletRequest request, String email) {
        Optional<String> accessTokenOpt = Optional.ofNullable(request.getHeader("Authorization"))
                .filter(t -> t.startsWith(BEARER))
                .map(t -> t.replace(BEARER, ""));

        accessTokenOpt.ifPresent(accessToken -> {
            long expireMs = getExpiration(accessToken).getTime() - System.currentTimeMillis();
            redisTemplate.opsForValue().set(ACCESS_PREFIX + accessToken, email, Duration.ofMillis(expireMs));
            redisTemplate.delete(REFRESH_PREFIX + email);
            log.info("로그아웃 처리 완료: email={}, 남은 AccessToken 만료={}ms", email, expireMs);
        });
    }



}
