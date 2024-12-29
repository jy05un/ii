package com.ii.object.entity;

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

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "password_auth")
public class PasswordAuth extends Base{

	@Id
	@Builder.Default
	private UUID id = UUID.randomUUID();
	
	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "auth_code")
	@Builder.Default
	private UUID authCode = UUID.randomUUID();
	
	@Column(name = "new_hashed_password")
	private String newHashedPassword;
	
}