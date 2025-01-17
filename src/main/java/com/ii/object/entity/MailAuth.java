package com.ii.object.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "mail_auth")
public class MailAuth extends Base{
	
	/*
	 * 회원가입 시 메일 인증 과정 중 인증코드를 저장하기 위한 테이블
	 */
	
	@OneToOne
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	@Column(name = "auth_code")
	@Builder.Default
	private UUID authCode = UUID.randomUUID();	// 생성된 UUID는 url에 담겨 인증url로 쓰임
	
}
