package com.ii.object.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
	@Builder.Default
	private UUID id = UUID.randomUUID();
	
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
	
	@Column(name = "mail_auth")
	@Builder.Default
	private Boolean mailAuth = false;
	
	@Column(name = "user_type")
	@Builder.Default
	private String OAuth2Type = "Registerd";
	
	@Column(name = "oauth2_id")
	private String OAuth2Id;
	
}
