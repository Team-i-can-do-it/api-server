package com.icando.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mail.naver.host}")
    private String host;

    @Value("${mail.naver.username}")
    private String username;

    @Value("${mail.naver.password}")
    private String password;

    @Value("${mail.naver.port}")
    private int port;

    @Value("${mail.naver.properties.mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.naver.properties.mail.smtp.ssl.trust}")
    private String smtpSslTrust;

    @Value("${mail.naver.properties.mail.smtp.ssl.enable}")
    private String smtpSslEnable;

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setJavaMailProperties(getMailProperties());

        return mailSender;
    }

    //java메일 API를 사용하기 위한 다양한 속성 설정
    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", smtpAuth);
        properties.setProperty("mail.smtp.ssl.trust", smtpSslTrust);
        properties.setProperty("mail.smtp.ssl.enable", smtpSslEnable);

        return properties;
    }
}
