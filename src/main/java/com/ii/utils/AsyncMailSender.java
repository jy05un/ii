package com.ii.utils;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@RequiredArgsConstructor
public class AsyncMailSender {

	@Async
	public void send(JavaMailSender javaMailSender, MimeMessage message) throws MessagingException {
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			send(javaMailSender, message, 0, e);
		}
		log.info("Mail Sent to " + message.getRecipients(MimeMessage.RecipientType.TO)[0]);
	}
	
	public void send(JavaMailSender javaMailSender, MimeMessage message, int retry, MailException e) throws MessagingException {
		if(retry > 3) {
			log.error("Mail did not sent to " + message.getRecipients(MimeMessage.RecipientType.TO)[0] + ", Error: " + e.getMessage());
			return;
		}
		try {
			javaMailSender.send(message);
		} catch(MailException e1) {
			send(javaMailSender, message, retry+1, e1);
		}
		log.info("Mail Sent to " + message.getRecipients(MimeMessage.RecipientType.TO)[0]);
	}
	
}
