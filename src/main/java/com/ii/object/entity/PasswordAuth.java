package com.ii.object.entity;

import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "password_auth")
public class PasswordAuth extends Base{
	
	/*
	 * 비밀번호 변경 시 사용되는 인증코드와 새로운 비밀번호를 저장하기 위한 테이블
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	@Column(name = "auth_code")
	@Builder.Default
	private UUID authCode = UUID.randomUUID();	// 생성된 UUID는 url에 담겨 인증url로 쓰임
	
	@Column(name = "new_hashed_password")
	private String newHashedPassword;			// 사용자가 바꾸고자 하는 비밀번호를 저장해놨다가 위 코드로 메일인증이 완료된 시점에 실제 유저 테이블에 반영됨
	
}