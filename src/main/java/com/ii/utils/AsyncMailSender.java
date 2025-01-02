package com.ii.utils;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AsyncMailSender {

	@Async
	public void send(JavaMailSender javaMailSender, MimeMessage message) {
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			send(javaMailSender, message, 0, e);
		}
	}
	
	public void send(JavaMailSender javaMailSender, MimeMessage message, int retry, MailException e) {
		if(retry > 3) {
			System.out.println(e.getMessage());
		}
		try {
			javaMailSender.send(message);
		} catch(MailException e1) {
			send(javaMailSender, message, retry+1, e1);
		}
	}
	
}
