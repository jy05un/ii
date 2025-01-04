package com.ii.object.entity;

import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "password_history")
public class PasswordHistory extends Base{
	
	/*
	 * 비밀번호 변경 기록을 남기는 테이블
	 * 최근 사용된 비밀번호 1건만을 기록함
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	@Column(name = "hashed_password")
	@NotNull
	private String hashedPassword;	// 가장 최근 사용한 비밀번호 1건을 기록함
	
	/* TODO
	 * 비밀번호 실패 횟수 추적
	 */
	@Builder.Default
	private Integer trial = 0;
	
}
