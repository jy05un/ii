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
	private String username = "II_" + UUID.randomUUID();
	
	@Column(name = "hashed_password")
	private String hashedPassword;
	
	@Column(unique = true)
	@Email
	private String email;
	
	@NotBlank
	@Size(min=2, max=16)
	@Builder.Default
	private String nickname = "익명의 이파리";
	
	@Builder.Default
	private String roles = "USER";
	
	@Column(name = "is_mail_authed")
	@Builder.Default
	private Boolean isMailAuthed = false;
	
	@Column(name = "user_type")
	@Builder.Default
	private String OAuth2Type = "Registerd";
	
	@Column(name = "oauth2_id")
	private String OAuth2Id;

	@OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private MailAuth mailAuth;
	
	@OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private PasswordAuth passwordAuth;
	
	@OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	private RefreshToken refreshToken;
	
	@OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
	@OrderBy("created_at ASC")
	@Builder.Default
	private List<PasswordHistory> passwordHistories = new ArrayList<>();
	
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
	
	public void addPasswordHistory(PasswordHistory passwordHistory) {
		this.passwordHistories.add(passwordHistory);
		passwordHistory.setUser(this);
	}
	
}
