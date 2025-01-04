package com.ii.object.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "users") // user가 예약어라 안됨 엣큥
public class User extends Base {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(unique = true)
	@NotNull
	@Builder.Default
	@Size(min=5, max=40)
	private String username = "II_" + UUID.randomUUID();	// oauth2 로그인 유저의 경우 최초 로그인시 username을 II_{UUID} 형태로 생성함
	
	@Column(name = "hashed_password")
	private String hashedPassword;	// 해시된 비밀번호
	
	@Column(unique = true)
	@Email
	private String email;	// unique한 이메일
	
	@NotBlank
	@Size(min=2, max=16)
	@Builder.Default
	private String nickname = "익명의 이파리";	// 기본 닉네임
	
	@Builder.Default
	private String roles = "USER";	// 기본 권한 USER
	
	@Column(name = "is_mail_authed")
	@Builder.Default
	private Boolean isMailAuthed = false;	// 기본 생성 시 메일인증 안된 상태
	
	@Column(name = "user_type")
	@Builder.Default
	private String OAuth2Type = "Registered";	// oauth2 로그인 유저는 Naver로, 회원가입한 유저는 Registered로 등록됨
	
	@Column(name = "oauth2_id")
	private String OAuth2Id;	// oauth2 로그인 유저의 id (naver 제공)

	@OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private MailAuth mailAuth;
	
	@OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private PasswordAuth passwordAuth;
	
	@OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private RefreshToken refreshToken;
	
	@OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private PasswordHistory passwordHistory;
	
	public void setMailAuth(MailAuth mailAuth) {
		this.mailAuth = mailAuth;
		mailAuth.setUser(this);
	}
	
	public void setPasswordAuth(PasswordAuth passwordAuth) {
		this.passwordAuth = passwordAuth;
		passwordAuth.setUser(this);
	}
	
	public void setRefreshToken(RefreshToken refreshToken) {
		this.refreshToken = refreshToken;
		refreshToken.setUser(this);
	}
	
	public void setPasswordHistory(PasswordHistory passwordHistory) {
		this.passwordHistory = passwordHistory;
		passwordHistory.setUser(this);
	}
	
}
