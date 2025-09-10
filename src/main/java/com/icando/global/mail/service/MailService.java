package com.icando.global.mail.service;

import com.icando.global.mail.dto.CodeDto;
import com.icando.global.mail.dto.EmailDto;
import com.icando.global.mail.exception.MailErrorCode;
import com.icando.global.utils.RedisUtil;
import com.icando.member.entity.Member;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberException;
import com.icando.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;

    @Value("${mail.naver.username}")
    String sendEmail;

    //보낼 이메일을 만드는 서비스
    public MimeMessage createCodeEmail(String email, String code) throws MessagingException {
       try {
           MimeMessage message = mailSender.createMimeMessage();
           message.addRecipients(MimeMessage.RecipientType.TO, email);
           message.setSubject("인증번호입니다.");
           message.setText("이메일 인증코드 : " + code);
           message.setFrom(sendEmail);
           return message;
       } catch (MessagingException e) {
           throw new MailSendException("이메일 생성 중 오류 발생", e);
       }
    }

    // 이메일을 보내는 서비스
    public void sendMail(MimeMessage email){
        try {
            log.info("이메일을 전송하였습니다.");
            mailSender.send(email);
        } catch (MailException mailException) {
            mailException.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    //인증 번호 만드는 서비스
    private String createAuthNumber() {
        return UUID.randomUUID().toString().substring(0, 6);
        //랜덤 인증번호 UUID 사용
    }

    //controller에서는 이 메서드만 활용하면 된다.
    public String sendCertificationMail(EmailDto emailDto)throws MessagingException {
        String code = createAuthNumber();
        //메일 생성
        sendMail(createCodeEmail(emailDto.getEmail(), code));
        redisUtil.setDataExpire(emailDto.getEmail(), code, 180L);

        Long ttl = redisUtil.getExpire(emailDto.getEmail(), TimeUnit.SECONDS);
        log.info("TTL for {}: {}", emailDto.getEmail(), ttl);


        return code;
    }

    @Transactional
    public MailErrorCode verifyEmailCode(CodeDto codeDto) {
        String redisCode = redisUtil.getData(codeDto.getEmail());
        Long ttl = redisUtil.getExpire(codeDto.getEmail(), TimeUnit.SECONDS);
        log.info(codeDto.getCode());
        log.info(redisCode);

        if(redisCode == null || ttl == -2) {
            return MailErrorCode.CODE_INVALID;
        } if (!redisCode.equals(codeDto.getCode())) {
            return MailErrorCode.CODE_IS_NOT_CORRECT;
        }

        redisUtil.setDataExpire("verified:" + codeDto.getEmail(), codeDto.getEmail(), 600);
        redisUtil.deleteData(codeDto.getEmail());

        return MailErrorCode.CODE_OK;
    }

}
