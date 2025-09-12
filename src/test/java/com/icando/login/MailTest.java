package com.icando.login;

import com.icando.global.mail.dto.CodeDto;
import com.icando.global.mail.dto.EmailDto;
import com.icando.global.mail.exception.MailErrorCode;
import com.icando.global.mail.service.MailService;
import com.icando.global.utils.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        // private 필드로 강제 세팅
        ReflectionTestUtils.setField(mailService, "sendEmail", "test@naver.com");
        //테스트에서 호출되지 않으면 UnnecessaryStubbingException 발생하기에 lenient() 처리
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("이메일 인증 코드 발송 + Redis 저장까지 확인")
    public void sendMailTest() throws MessagingException {

        //given
        EmailDto emailDto = EmailDto.builder()
                .email("test@gmail.com")
                .build();

        //when
        String code = mailService.sendCertificationMail(emailDto);

        //then
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
        verify(redisUtil).setDataExpire(eq(emailDto.getEmail()), eq(code), eq(180L));

        assertThat(code).hasSize(6);
    }

    @Test
    @DisplayName("메일 인증 성공")
    public void successMail() {
        String email = "test@gmail.com";
        CodeDto codeDto = CodeDto.builder()
                .email(email)
                .code("1A3C56")
                .build();

        when(redisUtil.getData(email)).thenReturn(codeDto.getCode());
        when(redisUtil.getExpire(eq(email), eq(TimeUnit.SECONDS))).thenReturn(120L);

        MailErrorCode errorCode = mailService.verifyEmailCode(codeDto);

        assertThat(errorCode).isEqualTo(MailErrorCode.CODE_OK);

        verify(redisUtil).setDataExpire(eq("verified:" + codeDto.getEmail()), eq(email), eq(600L));
        verify(redisUtil).deleteData(email);
    }

    @Test
    @DisplayName("Redis에 값 있는지 확인")
    public void sendMailTestRedis() {
        String email = "test@gmail.com";
        CodeDto codeDto = CodeDto.builder()
                .email(email)
                .code("1A3C56")
                .build();

        when(redisUtil.getData(email)).thenReturn(null);
        when(redisUtil.getExpire(email, TimeUnit.SECONDS)).thenReturn(-2L);

        MailErrorCode errorCode = mailService.verifyEmailCode(codeDto);

        assertThat(errorCode).isEqualTo(MailErrorCode.CODE_INVALID);

        verify(redisUtil, never()).deleteData(any());
    }

    @Test
    @DisplayName("코드 불일치")
    public void codeError() {

        String email = "test@gmail.com";
        CodeDto codeDto = CodeDto.builder()
                .email(email)
                .code("1A3C56")
                .build();

        when(redisUtil.getData(email)).thenReturn("8C2KS9");
        when(redisUtil.getExpire(email, TimeUnit.SECONDS)).thenReturn(120L);

        MailErrorCode errorCode = mailService.verifyEmailCode(codeDto);

        assertThat(errorCode).isEqualTo(MailErrorCode.CODE_IS_NOT_CORRECT);
        verify(redisUtil, never()).deleteData(any());

    }



}
