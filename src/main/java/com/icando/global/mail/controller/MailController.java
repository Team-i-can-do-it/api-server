package com.icando.global.mail.controller;

import com.icando.global.error.core.ErrorResponse;
import com.icando.global.mail.dto.CodeDto;
import com.icando.global.mail.dto.EmailDto;
import com.icando.global.mail.exception.MailErrorCode;
import com.icando.global.mail.exception.MailException;
import com.icando.global.mail.exception.MailSuccessCode;
import com.icando.global.mail.service.MailService;
import com.icando.global.success.SuccessResponse;
import com.icando.member.exception.MemberErrorCode;
import com.icando.member.exception.MemberSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
@Tag(
    name = "메일 관련 API",
    description = "메일에 관련된 컨트롤러입니다."
)
public class MailController {

    private final MailService mailService;

    @Operation(
        summary = "메일 전송",
        description = "메일을 전송하는 컨트롤러"
    )
    @PostMapping("/code/request")
    public ResponseEntity<SuccessResponse> mailAuthentication(@Valid @RequestBody EmailDto emailReq) throws MessagingException {

        mailService.sendCertificationMail(emailReq);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(MailSuccessCode.MAIL_SUCCESSD_SEND));
    }

    @Operation(
        summary = "이메일 인증 코드 인증",
        description = "받은 코드를 인증하는 컨트롤러입니다."
    )
    @PostMapping("/code/verify")
    public ResponseEntity<?> verifyEmailCode(@Valid @RequestBody CodeDto emailReq) {

        MailErrorCode mailErrorCode = mailService.verifyEmailCode(emailReq);

        if(mailErrorCode == MailErrorCode.CODE_INVALID) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.of(mailErrorCode));
        }
        if(mailErrorCode == MailErrorCode.CODE_IS_NOT_CORRECT) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.of(mailErrorCode));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(MailSuccessCode.MAIL_VERIFIED_SUCCESS));
    }
}
