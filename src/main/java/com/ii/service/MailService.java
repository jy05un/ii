package com.ii.service;

import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
	
	private final JavaMailSender javaMailSender;
    private static final String senderEmail = "ii.con.auth@gmail.com";

    public MimeMessage createMail(String mail, UUID auth_code) throws MessagingException {
    	MimeMessage message = javaMailSender.createMimeMessage();
    	
    	message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("II 서비스 이메일 인증 요청");
        String body = "";
        body += "<h2>" + "계정 인증 Url입니다." + "</h2>";
        body += "<h1><a href='http://localhost:8080/auth/register/mail?authCode=" + auth_code + "'>메일 인증<a></h1>";
        message.setText(body,"UTF-8", "html");
        
		return message;
    }
    
    public void sendMail(String mail, UUID auth_code) throws MessagingException {
        MimeMessage message;
        message = createMail(mail, auth_code);
        javaMailSender.send(message);
    }

}
