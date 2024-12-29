package com.ii.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${app.base-url}")
    private final String baseUrl;

    public MimeMessage createAuthMail(String mail, UUID auth_code) throws MessagingException {
    	MimeMessage message = javaMailSender.createMimeMessage();
    	
    	message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("II 서비스 이메일 인증 요청");
        String body = "";
        body += "<h2>" + "계정 인증 Url입니다." + "</h2>";
        body += "<h1><a href='" + baseUrl + "/auth/email?authCode=" + auth_code + "'>메일 인증<a></h1>";
        message.setText(body,"UTF-8", "html");
        
		return message;
    }
    
    public void sendAuthMail(String mail, UUID auth_code) throws MessagingException {
        MimeMessage message = createAuthMail(mail, auth_code);
        javaMailSender.send(message);
    }
    
    public MimeMessage createPasswordUpdateMail(String mail, UUID auth_code) throws MessagingException {
    	MimeMessage message = javaMailSender.createMimeMessage();
    	
    	message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("II 서비스 이메일 인증 요청");
        String body = "";
        body += "<h2>" + "비밀번호 변경을 위한 이메일 인증 Url입니다." + "</h2>";
        body += "<h1><a href='" + baseUrl + "/auth/password/update?authCode=" + auth_code + "'>메일 인증<a></h1>";
        message.setText(body,"UTF-8", "html");
        
		return message;
    }
    
    public void sendPasswordUpdateMail(String mail, UUID auth_code) throws MessagingException {
        MimeMessage message = createPasswordUpdateMail(mail, auth_code);
        javaMailSender.send(message);
    }
    
    public MimeMessage createUsernameMail(String mail, String username) throws MessagingException {
    	MimeMessage message = javaMailSender.createMimeMessage();
    	
    	message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("II 서비스 사용자 이름 찾기");
        String body = "";
        body += "<h2>" + "사용자 이름 찾기 요청에 대한 이메일입니다." + "</h2>";
        body += "<h1>" + username + "</h1>";
        message.setText(body,"UTF-8", "html");
        
		return message;
    }
    
    public void sendUsernameMail(String mail, String username) throws MessagingException {
    	MimeMessage message = createUsernameMail(mail, username);
    	javaMailSender.send(message);
    }
    
    public MimeMessage createPasswordFindMail(String mail, String newPassword) throws MessagingException {
    	MimeMessage message = javaMailSender.createMimeMessage();
    	
    	message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("II 패스워드 찾기");
        String body = "";
        body += "<h2>" + "패스워드 찾기 요청에 대한 이메일입니다." + "</h2>";
        body += "<h1>" + newPassword + "</h1>";
        message.setText(body,"UTF-8", "html");
        
		return message;
    }
    
    public void sendPasswordFindMail(String mail, String newPassword) throws MessagingException {
    	MimeMessage message = createPasswordFindMail(mail, newPassword);
    	javaMailSender.send(message);
    }

}
